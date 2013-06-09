package nl.tudelft.in4150.group18.ui;

public class LabelEdge extends org.jgrapht.graph.DefaultEdge {
	
	private static final long serialVersionUID = 1L;
	
	private String label;
	
	public LabelEdge(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
	