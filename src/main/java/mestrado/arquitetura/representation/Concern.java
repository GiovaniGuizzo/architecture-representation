package mestrado.arquitetura.representation;

public class Concern {

	private final String name;

	public Concern(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
}