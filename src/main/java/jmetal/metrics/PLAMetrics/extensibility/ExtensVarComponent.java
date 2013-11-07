package jmetal.metrics.PLAMetrics.extensibility;

import arquitetura.representation.Package;
import arquitetura.representation.Class;

public class ExtensVarComponent {

	private final Package component;

	public ExtensVarComponent(Package component) {
		this.component = component;
	}
	
	public float getValue() {
		float result = 0;
		for (Class class_ : component.getClasses())
			result += new ExtensClass(class_).getValue();
		
		return result;
	}
}