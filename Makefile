API=crudl.sadl
#API=crudl.smithy


all:: run

test:: build
	@echo OK!!!
	(cd generated; mvn exec:java -Dmy.mainClass="Test")

run:: build
	(cd generated; mvn exec:java)

build:: gen
	(cd generated; mvn compile)

gen: generated generated/src/main/java/CrudlController.java generated/src/main/java/Main.java generated/src/main/java/Test.java

generated/src/main/java/CrudlController.java: CrudlController.java
	cp -p CrudlController.java generated/src/main/java/CrudlController.java

generated/src/main/java/Main.java: $(API)
	sadl -g java-server -o generated $(API)
	sadl -g java-client -o generated $(API)

generated/src/main/java/Test.java: Test.java
	cp -p Test.java generated/src/main/java/Test.java

generated:
	mkdir -p generated/src/main/java

clean::
	rm -rf generated
