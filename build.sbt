name                := "ScalaLab"

version             := ""

organization        := ""

scalaVersion        := "2.12.10" 

javaOptions   ++= Seq("-Xss", "2M", "-Xmx", "4G")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")


scalacOptions ++= Seq("-deprecation", "-unchecked", "-opt:l:inline",
 "-opt:l:inline", "closure-invocations", "l:method")

description         := "A MATLAB-like environment)"

exportJars := true
classpathTypes += "maven-plugin"

libraryDependencies += "org.nd4j" % "nd4j-native" % "1.0.0-beta5" classifier "" classifier "linux-x86_64"
libraryDependencies += "org.bytedeco.javacpp-presets" % "openblas" % "0.2.20-1.4.1" classifier "" classifier "linux-x86_64"


libraryDependencies += "org.nd4j" % "nd4j-native" % "1.0.0-beta5" classifier "" classifier "windows-x86_64"
libraryDependencies += "org.bytedeco.javacpp-presets" % "openblas" % "0.2.20-1.4.1" classifier "" classifier "windows-x86_64"


libraryDependencies += "org.nd4j" % "nd4j-native" % "1.0.0-beta5" classifier "" classifier "macosx-x86_64"
libraryDependencies += "org.bytedeco.javacpp-presets" % "openblas" % "0.2.20-1.4.1" classifier "" classifier "macosx-x86_64"


libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-beta5"


  
val dependentJarDirectory = settingKey[File]("location of the unpacked jars")
dependentJarDirectory := target.value / "dependent-jars"

val createDependentJarDirectory = taskKey[File]("create the dependent-jars directory")

createDependentJarDirectory :=  {
  sbt.IO.createDirectory(dependentJarDirectory.value)
  dependentJarDirectory.value
}
  
val excludes = List(".git",  "META-INF/*.SF",  "META-INF/*.DSA", "META-INF/*.RSA")
  
def unpackFilter(target: File) = new NameFilter {
    def accept(name: String) = {
    !excludes.exists( x => name.toLowerCase().startsWith(x)) && 
    !file(target.getAbsolutePath + "/" + name).exists
        }
 }
 
def unpack(target: File, f: File, log: Logger) = {
 log.debug("unpacking "+ f.getName)
 if (f.isDirectory) 
    sbt.IO.copyDirectory(f, target)
 else
    sbt.IO.unzip(f, target, filter = unpackFilter(target))
    }
    
def isLocal(f: File, base: File) = sbt.IO.relativize(base, f).isDefined


 def isValid(f:File, base:File) = true
 //{
 //   if ((isLocal(f, base)) && (f.getName.contains("openblas")==true))
 //      false
 //   else true
 //   }

def unpackJarSeq(files: Seq[File], target: File, base: File, local: Boolean, log: Logger) = {
 files.filter(f=> (local==isValid(f, base))  ).map(f=>  unpack(target, f, log))
 }
 
 val unpackJars = taskKey[Seq[_]]("unpacks a dependent jars into target/dependent-jars")
 
unpackJars := {
  val dir = createDependentJarDirectory.value
  val log = streams.value.log
  val bd = (baseDirectory in ThisBuild).value
  val classpathJars = Attributed.data((dependencyClasspath in Runtime).value)
  unpackJarSeq(classpathJars, dir, bd, true, log)
  }
  
val createUberJar = taskKey[File]("create jar which will run")
 
createUberJar := {
  val bd = (baseDirectory in ThisBuild).value
  val log = streams.value.log
  val output = target.value / "ScalaLabDL4j.jar"
  val classpathJars = Attributed.data((dependencyClasspath in Runtime).value)
  sbt.IO.withTemporaryDirectory( td => {
    unpackJarSeq(classpathJars, td, bd, true, log)
    create (dependentJarDirectory.value, td, (baseDirectory.value / "src/main/uber"), output)
    })
    output
  }
   
def create(depDir: File, localDir: File, extractDir: File, buildJar: File) = {
  def files(dir: File) = {
    val fs = (dir ** "*").get.filter(d => d != dir)
    fs.map( x => (x, x.relativeTo(dir).get.getPath))
    }
    
   sbt.IO.zip(files(localDir) ++ files(depDir) ++ files(extractDir), buildJar)
   }
   /*
trait UberJarRunner {
  def start(): Unit
  def stop(): Unit
}

class MyUberJarRunner(uberJar: File) extends UberJarRunner {
  var p: Option[Process] = None
  def start(): Unit = {
    p = Some(Fork.java.fork(ForkOptions(),
             Seq("-cp", uberJar.getAbsolutePath, "Global")))
  }
  def stop(): Unit = p foreach (_.destroy())
}

*/
val runUberJar = taskKey[Int]("run the uber jar")
runUberJar := {
  val uberJar = createUberJar.value
  val options = ForkOptions()
  val arguments = Seq("-jar", uberJar.getAbsolutePath)
  Fork.java(options, arguments)
  }
  
  
  
  
 
val classPath = Seq(
  "./lib/ApacheCommonMaths.jar",
     "./lib/JASYMCA.jar",
    "./lib/JFreeChart.jar",
    "./lib/JfreeCommon.jar",
     "./lib/jfreesvg.jar",
     "./lib/jgraph.jar",
      "./lib/itext.jar",
      "./lib/ejml.jar",
       "./lib/LBFGS.jar",
         "./extralib/openblas.jar",
     "./lib/MTJColtSGTJCUDA.jar",
     "./lib/NumericalRecipesNUMAL.jar",
     "./lib/PDFRenderer.jar",
     "./lib/RSyntaxTextArea.jar",
     "./lib/akka-actor.jar",
    "./lib/antlr-2.7.7.jar",
     "./lib/apidoc.jar",
     "./lib/arpack_combo-0.1.jar",
     "./lib/asm-all-4.1.jar",
     "./lib/cglib-nodep-2.2.jar",
     "./lib/config.jar",
     "./lib/diffutils.jar",
     "./lib/f2jutil.jar",
     "./lib/fjbg.jar",
     "./lib/funclate-131.jar",
     "./lib/gsl-linux-x86.jar",
     "./lib/gsl-linux-x86_64.jar",
     "./lib/gsl-macosx-x86_64.jar",
     "./lib/gsl-windows-x86.jar",
     "./lib/gsl-windows-x86_64.jar",
     "./lib/gsl.jar",
    "./lib/Jeigen-onefat.jar",
    "./lib/hamcrest-core-1.3.jar",
    "./lib/itext-2.1.5.jar",
     "./lib/jSciJPlasmaJSparseJTransforms.jar",
     "./lib/jarjar-1.1.jar",
     "./extralib/javacpp.jar",
     "./lib/jdk6Help.jar",
     "./lib/jhall.jar",
     "./lib/jline.jar",
     "./lib/jna-4.0.0.jar",
     "./lib/jsearch.jar ",
     "./lib/jsyntaxpane.jar",
     "./lib/matlabscilab.jar",
     "./lib/netlib-java-0.9.3.jar",
     "./lib/objenesis-1.2.jar",
     "./lib/optimization.jar",
     "./lib/scala-actors-migration.jar",
     "./lib/scala-actors.jar",
     "./lib/scala-compiler.jar",
     "./lib/scala-continuations-library.jar",
     "./lib/scala-continuations-plugin.jar",
     "./lib/scala-library.jar",
     "./lib/scala-parser-combinators.jar",
     "./lib/scala-reflect.jar",
     "./lib/scala-swing.jar",
     "./lib/scala-xml.jar",
     "./lib/scalaHelp.jar",
     "./lib/scalap.jar",
     "./lib/txt2xhtml.jar",
     "./lib/stringtemplate-3.2.1.jar",
     "./lib/xmlgraphics-commons.jar"
    )
    
    

packageOptions += Package.ManifestAttributes(
  "Class-Path" -> classPath.mkString(" "),
  "Main-Class" -> "scalaExec.scalaLab.scalaLab"
)
   

