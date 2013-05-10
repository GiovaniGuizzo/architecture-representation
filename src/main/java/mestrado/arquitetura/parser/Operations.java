package mestrado.arquitetura.parser;

/**
 * 
 * 
 * @author edipofederle
 *
 */
public class Operations {
	
	 private DocumentManager doc;
	 private ClassOperations classOperation;
	 private AssociationOperations associationOperation;
	 private PackageOperations packageOperaiton;
	 private DependencyOperations dependencyOperation;
	 private UsageOperations usageOperation;
	 private GeneralizationOperations generalizationOperations;
	 private CompositionOperations compositionOperations;
	 private AggregationOperations aggregationOperations;

	 public Operations(DocumentManager doc2) {
		 this.doc = doc2;
		 createClassOperation();
		 createAssociationOperations();
		 createPackageOperations();
		 createDependencyOperations();
		 createUsageOperations();
		 createGeneralizationOperations();
		 createCompositionOperations();
		 createAggrationOperations();
	 }

	private void createAggrationOperations() {
		this.aggregationOperations = new AggregationOperations(doc);
	}

	private void createCompositionOperations() {
		this.compositionOperations = new CompositionOperations(doc);
	}

	private void createGeneralizationOperations() {
		this.generalizationOperations = new GeneralizationOperations(doc);
	}

	private void createUsageOperations() {
		this.usageOperation = new UsageOperations(doc);
	}

	private void createDependencyOperations() {
		this.dependencyOperation = new DependencyOperations(doc);
	}

	private void createPackageOperations() {
		this.packageOperaiton = new PackageOperations(doc);
	}

	private void createAssociationOperations() {
		this.associationOperation = new AssociationOperations(doc);
	}

	private void createClassOperation() {
		this.classOperation = new ClassOperations(doc);
	}

	public ClassOperations forClass() { return classOperation; }
	public AssociationOperations forAssociation(){ return associationOperation; }
	public PackageOperations forPackage(){ return packageOperaiton; }
	public DependencyOperations forDependency(){ return dependencyOperation; }
	public UsageOperations forUsage(){ return usageOperation; };
	public GeneralizationOperations forGeneralization(){ return generalizationOperations; }
	public CompositionOperations forComposition(){ return compositionOperations; }
	public AggregationOperations forAggregation(){ return aggregationOperations; }

}