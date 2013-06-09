package nl.tudelft.in4150.group18.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To view the messages being send between nodes.
 * 
 * @author paul
 */
public class GraphView extends JFrame {

	private static final long serialVersionUID = 6966780700360086674L;
	private static final Logger log = LoggerFactory.getLogger(GraphView.class);

	private static final GraphView _instance = new GraphView();
	public static GraphView getInstance() { 
		return _instance;
	}

	private static Object sharedLock = new Object();

	private ListenableGraph<String, LabelEdge> g;
	private JGraphModelAdapter<String, LabelEdge> a;

	private GraphView() {
		super("Graph view");
		setVisible(false);
		setSize(700, 600);
		setLocation(670, 100);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		g = new ListenableDirectedGraph<String, LabelEdge>(LabelEdge.class);
		a = new JGraphModelAdapter<String, LabelEdge>(g);
		JGraph jgraph = new JGraph(a);
		this.getContentPane().add(jgraph);
	}

	public synchronized void setVertexColor(String id, Color color) {
		log.info("setVertexColor(\"" + id + "\", " + color + ")");
		
		synchronized(sharedLock) {
			g.addVertex(id);
			
			DefaultGraphCell cell = a.getVertexCell(id);
			AttributeMap attr = cell.getAttributes();
			
			GraphConstants.setBackground(attr, color);
	
			Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<DefaultGraphCell, AttributeMap>();
			cellAttr.put(cell, attr);
			a.edit(cellAttr, null, null, null);
		}
	}
	
	public void addEdge(String id1, String id2, String label) {
		log.info("addEdge(\"" + id1 + "\", \"" + id2 + "\", \"" + label + "\")");
		
		synchronized(sharedLock) {
			g.addVertex(id1);
			g.addVertex(id2);
			
			LabelEdge e = new LabelEdge(label);
			g.addEdge(id1, id2, e);
			
			DefaultEdge cell = a.getEdgeCell(e);
			AttributeMap attr = cell.getAttributes();
			
			if (label.contains("ATTACK"))
				GraphConstants.setLineColor(attr, Color.red);
			else
				GraphConstants.setLineColor(attr, Color.green);
			
			Map<DefaultEdge, AttributeMap> cellAttr = new HashMap<DefaultEdge, AttributeMap>();
			cellAttr.put(cell, attr);
			a.edit(cellAttr, null, null, null);
		}
	}
	
	public void position() {
		log.debug("position()");
		
		synchronized (sharedLock) {
			try {
				Set<String> s = g.vertexSet();
				int j = s.size();
				double i = 0;
				for (String v : s) {
					DefaultGraphCell graphCell = a.getVertexCell(v);
					AttributeMap attr = graphCell.getAttributes();
					
					// put vertices in circle
					double x = 250 * Math.cos(2 * Math.PI * i / j) + 250 + 100 * Math.random();
					double y = 200 * Math.sin(2 * Math.PI * i / j) + 200 + 100 * Math.random();
					GraphConstants.setBounds(attr, new Rectangle2D.Double(x, y, 50, 40));
					
					Map<DefaultGraphCell, AttributeMap> graphCellAttr = new HashMap<DefaultGraphCell, AttributeMap>();
					graphCellAttr.put(graphCell, attr);
					a.edit(graphCellAttr, null, null, null);
					i++;
					/*
					Set<LabelEdge> d = g.edgesOf(v);
					for (LabelEdge e : d) {
						DefaultEdge edgeCell = a.getEdgeCell(e);
						attr = edgeCell.getAttributes();
						
						// move labels away
						//GraphConstants.setLabelPosition(attr, new Point2D.Double(-2000, -2000));
						//GraphConstants.setLineColor(attr, new Color(
						//		(int) (255 * Math.random()),
						//		(int) (255 * Math.random()),
						//		(int) (255 * Math.random())
						//	)
						//);
						if (e.toString().contains("ATTACK"))
							GraphConstants.setLineColor(attr, Color.red);
						else
							GraphConstants.setLineColor(attr, Color.green);
						
						Map<DefaultEdge, AttributeMap> edgeCellAttr = new HashMap<DefaultEdge, AttributeMap>();
						edgeCellAttr.put(edgeCell, attr);
						a.edit(edgeCellAttr, null, null, null);
						i++;
					}*/
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}