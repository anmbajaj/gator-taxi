default: clean
	javac -sourcepath . -d . ./gatorTaxi.java

clean:
	rm -rf *.class
