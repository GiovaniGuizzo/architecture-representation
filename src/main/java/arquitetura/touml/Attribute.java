package arquitetura.touml;

import java.util.ArrayList;
import java.util.List;

import arquitetura.helpers.UtilResources;
import arquitetura.representation.Concern;

/**
 * 
 * @author edipofederle<edipofederle@gmail.com>
 *
 */
public class Attribute {
	
	private String id;
	private String name;
	private VisibilityKind visibility;
	private Types.Type type;
	private List<Concern> concerns = new ArrayList<Concern>();
	private boolean generateVisualAttribute;
	
	/**
	 * @return the generateVisualAttribute
	 */
	public boolean isGenerateVisualAttribute() {
		return generateVisualAttribute;
	}

	/**
	 * @return the concerns
	 */
	public List<Concern> getConcerns() {
		return concerns;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the visibility
	 */
	public String getVisibility() {
		return visibility.getName();
	}
	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(VisibilityKind visibility) {
		this.visibility = visibility;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type.getName();
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Types.Type type) {
		this.type = type;
	}
	
	public static Attribute create() {
		Attribute attr =  new Attribute();
		attr.setId(UtilResources.getRandonUUID());
		return attr;
		
	}
	private void setId(String randonUUID) {
		this.id = randonUUID;
	}

	public Attribute withName(String name) {
		this.name = name;
		return this;
	}

	public Attribute withVisibility(VisibilityKind visibility) {
		this.visibility = visibility;
		return this;
	}
	public Attribute withType(Types.Type type) {
		this.type = type;
		return this;
	}

	public Attribute withConcerns(List<Concern> ownConcerns) {
		this.concerns = ownConcerns;
		return this;
	}

	/**
	 * Gera ou não graticamente o atributo
	 * 
	 * @param generatVisualAttribute
	 * @return
	 */
	public Attribute grafics(boolean generatVisualAttribute) {
		this.generateVisualAttribute = generatVisualAttribute;
		return this;
	}

}