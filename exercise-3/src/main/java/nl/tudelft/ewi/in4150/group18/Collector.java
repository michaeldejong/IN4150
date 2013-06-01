package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class Collector {
	
	private volatile Node root = null;
	
	private final Object lock = new Object();
	
	public void collect(Type value, List<Address> path) {
		synchronized (lock) {
			if (root == null) {
				Address key = path.get(0);
				
				if (path.size() == 1) {
					root = new Node(value, key);
				}
				else {
					root = new Node(null, key);
					root.add(value, path);
				}
			}
			else {
				if (path.size() == 1) {
					root.setValue(value);
				}
				else {
					root.add(value, path);
				}
			}
		}
	}
	
	public Type calculateMajority() {
		synchronized (lock) {
			Type decision = calculateMajority(root);
			root = null;
			return decision;
		}
	}
	
	private Type calculateMajority(Node node) {
		int attack = 0;
		int retreat = 0;
		
		if (node.children.isEmpty()) {
			return node.sent.get();
		}
		
		for (Node child : node.children.values()) {
			Type childDecision = calculateMajority(child);
			if (childDecision == Type.ATTACK) {
				attack++;
			}
			else if (childDecision == Type.RETREAT) {
				retreat++;
			}
		}
		
		return attack > retreat ? Type.ATTACK : Type.RETREAT;
	}
	
	private static class Node {
		private AtomicReference<Type> sent;
		private final Address node;
		private final Map<Address, Node> children;
		
		public Node(Type sent, Address node) {
			this.children = Maps.newConcurrentMap();
			this.sent = new AtomicReference<>(sent);
			this.node = node;
		}
		
		public void setValue(Type value) {
			this.sent.set(value);
		}
		
		public void add(Type sent, List<Address> path) {
			Address remove = path.get(0);
			path = path.subList(1, path.size());
			if (!remove.equals(node)) {
				throw new IllegalArgumentException("Illegal path!");
			}
			
			if (path.isEmpty()) {
				setValue(sent);
				return;
			}
			
			Address key = path.get(0);
			
			synchronized (children) {
				if (children.containsKey(key)) {
					children.get(key).add(sent, path);
				}
				else {
					Node value = new Node(null, key);
					children.put(key, value);
					value.add(sent, path);
				}
			}
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[" + sent + " - " + node);
			if (!children.isEmpty()) {
				builder.append(" - " + Joiner.on(",").join(children.values()));
			}
			builder.append("]");
			return builder.toString();
		}
	}
	
	
}
