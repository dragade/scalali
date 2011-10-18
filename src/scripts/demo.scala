/* This is a sample script you can run in the scala REPL to play with ScalaLi */
/* You can also run this from within sbt */
/* sbt */
/* compile */
/* console */
/* :load ../demo.scala */

println("ScalaLi demo")
import com.dragade.scalali.ScalaLi
import com.dragade.scalali.Person
import java.util.Scanner

// get your API keys from http://developer.linkedin.com
val apiKey = ""
val secretKey = ""

val scalali = new ScalaLi(apiKey, secretKey)
val (url, requestToken) = scalali.initialize()
println("\n\nGo hit URL %s and then come back with the verifier code.".format(url))
print("Verifier: ")

val verifier = (new Scanner(System.in)).next
println("\nUsing verifier " + verifier)
val accessToken = scalali.verify(requestToken,verifier)

println("\nFetching your profile\n")
val myProfile = scalali.myProfile(accessToken)
myProfile match {
  case None => println("No profile found for you!")
  case Some(p) => println("Name: %s %s".format(p.firstName, p.lastName))
}

println("Fetching your connections and partitioning into those with pics and headlines and those without\n")
val conns = scalali.myConnections(accessToken)
val (complete, incomplete) = conns.partition(p => p.picture.isDefined &&  p.headline.isDefined)
complete.foreach(p => println("Name:%s %s\t%s\t%s".format(p.firstName, p.lastName, p.headline.get, p.picture.get)))

//Make a call to the volemort API if you have permissions
//scalali.voldemort(accessToken, "twitterHandles", Set("dragade","larryyuan"))
