# JaCoCo Demo Application

This application is to exemplify some issues with JaCoCo and Spring Boot Uber JARs

### Steps to reproduce

* JaCoCo version:  0.8.5
* Operating system: Windows
* Tool integration: Agent / CLI
* Complete executable reproducer: https://github.com/tomcruise81/jacoco-demo.git
* Steps: (what exactly are you doing with the above reproducer?)
    ```bash
    git clone https://github.com/tomcruise81/jacoco-demo.git
    cd jacoco-demo
    mvn package
    targetJar=$(ls target/*.jar | sed 's|target/||')

    # For Windows, under Git Bash, you may also need to convert Cygwin-type paths to Windows paths
    curDir=$(pwd)
    unameOut="$(uname -s)"
    case "${unameOut}" in
        CYGWIN*)    ;&
        MINGW*)     curDir=$(cygpath -m ${curDir});;
    esac

    # Download the appropriate JaCoCo JARs
    curl -k -L https://search.maven.org/remotecontent?filepath=org/jacoco/org.jacoco.cli/0.8.5/org.jacoco.cli-0.8.5-nodeps.jar -o ${curDir}/jacoco-cli.jar
    curl -k -L https://search.maven.org/remotecontent?filepath=org/jacoco/org.jacoco.agent/0.8.5/org.jacoco.agent-0.8.5-runtime.jar -o ${curDir}/jacoco-agent-runtime.jar

    # Run JaCoCo
    java -jar ${curDir}/jacoco-cli.jar instrument ${curDir}/target/${targetJar} --dest ${curDir}/instrumented
    java -javaagent:${curDir}/jacoco-agent-runtime.jar=destfile=${curDir}/jacoco.exec,excludes=*,output=file,dumponexit=true -jar ${curDir}/instrumented/${targetJar}
    ```

### Expected behavior

My expectation is that the pre-instrumented Uber JAR should continue to be runnable and running it with the **javaagent** enabled should allow code coverage output.

I've had to go about it this way instead of just running the main Uber JAR with the **javaagent** due to the output of just running with the **javaagent** being unusable:

```bash
java -javaagent:${curDir}/jacoco-agent-runtime.jar=destfile=${curDir}/jacoco.exec,excludes=*,output=file,dumponexit=true -jar ${curDir}/target/${targetJar}
java -jar ${curDir}/jacoco-cli.jar report jacoco.exec --classfiles=${curDir}/target/${targetJar} --html ${curDir}/coverage-html
```
resulting in:

    [INFO] Loading execution data file C:\test\jacoco-demo\jacoco.exec.
    Exception in thread "main" java.io.IOException: Error while analyzing C:\test\jacoco-demo\target\jacoco-demo-0.0.1-SNAPSHOT.jar@BOOT-INF/lib/log4j-api-2.12.1.jar@org/apache/logging/log4j/util/Base64Util.class.
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzerError(Analyzer.java:162)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeClass(Analyzer.java:134)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeClass(Analyzer.java:157)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeAll(Analyzer.java:193)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeZip(Analyzer.java:265)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeAll(Analyzer.java:196)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeZip(Analyzer.java:265)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeAll(Analyzer.java:196)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeAll(Analyzer.java:226)
            at org.jacoco.cli.internal.commands.Report.analyze(Report.java:110)
            at org.jacoco.cli.internal.commands.Report.execute(Report.java:84)
            at org.jacoco.cli.internal.Main.execute(Main.java:90)
            at org.jacoco.cli.internal.Main.main(Main.java:105)
    Caused by: java.lang.IllegalStateException: Can't add different class with same name: org/apache/logging/log4j/util/Base64Util
            at org.jacoco.cli.internal.core.analysis.CoverageBuilder.visitCoverage(CoverageBuilder.java:106)
            at org.jacoco.cli.internal.core.analysis.Analyzer$1.visitEnd(Analyzer.java:99)
            at org.jacoco.cli.internal.asm.ClassVisitor.visitEnd(ClassVisitor.java:326)
            at org.jacoco.cli.internal.core.internal.flow.ClassProbesAdapter.visitEnd(ClassProbesAdapter.java:100)
            at org.jacoco.cli.internal.asm.ClassReader.accept(ClassReader.java:692)
            at org.jacoco.cli.internal.asm.ClassReader.accept(ClassReader.java:400)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeClass(Analyzer.java:116)
            at org.jacoco.cli.internal.core.analysis.Analyzer.analyzeClass(Analyzer.java:132)
            ... 11 more

### Actual behavior
    java -javaagent:${curDir}/jacoco-agent-runtime.jar=destfile=${curDir}/jacoco.exec,excludes=*,output=file,dumponexit=true -jar ${curDir}/instrumented/${targetJar}
    Exception in thread "main" java.lang.IllegalStateException: Failed to get nested archive for entry BOOT-INF/lib/spring-boot-starter-actuator-2.2.4.RELEASE.jar
            at org.springframework.boot.loader.archive.JarFileArchive.getNestedArchive(JarFileArchive.java:113)
            at org.springframework.boot.loader.archive.JarFileArchive.getNestedArchives(JarFileArchive.java:87)
            at org.springframework.boot.loader.ExecutableArchiveLauncher.getClassPathArchives(ExecutableArchiveLauncher.java:69)
            at org.springframework.boot.loader.Launcher.launch(Launcher.java:50)
            at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:52)
    Caused by: java.io.IOException: Unable to open nested jar file 'BOOT-INF/lib/spring-boot-starter-actuator-2.2.4.RELEASE.jar'
            at org.springframework.boot.loader.jar.JarFile.getNestedJarFile(JarFile.java:261)
            at org.springframework.boot.loader.jar.JarFile.getNestedJarFile(JarFile.java:247)
            at org.springframework.boot.loader.archive.JarFileArchive.getNestedArchive(JarFileArchive.java:109)
            ... 4 more
    Caused by: java.lang.IllegalStateException: Unable to open nested entry 'BOOT-INF/lib/spring-boot-starter-actuator-2.2.4.RELEASE.jar'. It has
    been compressed and nested jar files must be stored without compression. Please check the mechanism used to create your executable jar file
            at org.springframework.boot.loader.jar.JarFile.createJarFileFromFileEntry(JarFile.java:287)
            at org.springframework.boot.loader.jar.JarFile.createJarFileFromEntry(JarFile.java:269)
            at org.springframework.boot.loader.jar.JarFile.getNestedJarFile(JarFile.java:258)
            ... 6 more

### Possible solution
```bash
git clone https://github.com/tomcruise81/jacoco-demo.git
cd jacoco-demo

# Download the appropriate JaCoCo JARs
curl -k -L https://search.maven.org/remotecontent?filepath=org/jacoco/org.jacoco.cli/0.8.5/org.jacoco.cli-0.8.5-nodeps.jar -o ${curDir}/jacoco-cli.jar
curl -k -L https://search.maven.org/remotecontent?filepath=org/jacoco/org.jacoco.agent/0.8.5/org.jacoco.agent-0.8.5-runtime.jar -o ${curDir}/jacoco-agent-runtime.jar

# For Windows, under Git Bash, you may also need to convert Cygwin-type paths to Windows paths
curDir=$(pwd)
unameOut="$(uname -s)"
case "${unameOut}" in
    CYGWIN*)    ;&
    MINGW*)     curDir=$(cygpath -m ${curDir});;
esac

# Package the runnable JAR
mvn clean package
uberJar=$(ls target/*.jar | sed 's|target/||')
uberJar=${curDir}/target/${uberJar}

# Package the sources JAR
mvn source:jar
sourcesJar=$(ls target/*-sources.jar | sed 's|target/||')
sourcesJar=${curDir}/target/${sourcesJar}

################################################################
# The purpose of this repo is to determine how to instrument a
# pre-existing Spring Boot Uber JAR. So the setup above is just
# to create the artifacts that are expected to be available for
# this use-case.
################################################################

# Determine the includes list; the sources JAR includes all expected source files, and their corresponding packages
# Instead of including every source file, just include their respective packages
includes=$(zipinfo -2 ${sourcesJar} -x META-INF/** | grep -E '*.java' | awk -F/ 'BEGIN { OFS = FS }; NF { NF -= 1 }; 1' | uniq | sed 's|/|.|g' | awk -v suffix=".*" '{print $0 suffix}' | paste -sd ":" -)

# Run JaCoCo
rm -f jacoco.exec
java -javaagent:${curDir}/jacoco-agent-runtime.jar=destfile=${curDir}/jacoco.exec,includes=${includes},output=file,dumponexit=true -jar ${uberJar}
# The following blows up with "java.lang.IllegalStateException: Can't add different class with same name:..."
# java -jar ${curDir}/jacoco-cli.jar report jacoco.exec --classfiles=${uberJar} --html ${curDir}/coverage-html

# Extract the Uber JAR and explicitly determine which class files to report on as per the includes list (i.e. source packages)
tempClassfilesDir=$(mktemp -d)
unzip ${uberJar} -d ${tempClassfilesDir}
includesFilter=$(echo ${includes} | sed 's/:/|/g')
classfiles=$(zipinfo -2 ${uberJar} | grep -E ${includesFilter} | grep -E '*\.class' | awk -F/ 'BEGIN { OFS = FS }; NF { NF -= 1 }; 1' | uniq | awk -v prefix="--classfiles=${tempClassfilesDir}/" -v suffix="/" '{print prefix $0 suffix}' | paste -sd " " -)

# Extract the source JAR so that source files can be linked during reporting
tempSourcefilesDir=$(mktemp -d)
unzip ${sourcesJar} -d ${tempSourcefilesDir}

# Do the actual reporting
htmlCoverageDir=${curDir}/reports/coverage-html
rm -rf ${htmlCoverageDir}
java -jar ${curDir}/jacoco-cli.jar report jacoco.exec ${classfiles} --sourcefiles=${tempSourcefilesDir} --html ${htmlCoverageDir}

# Cleanup
rm -rf ${tempSourcefilesDir}
rm -rf ${tempClassfilesDir}
```
