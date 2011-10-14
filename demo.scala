/* This is a sample script you can run in the scala REPL to play with ScalaLi */
/* You can also run this from within sbt */
/* sbt */
/* compile */
/* console */
/* :load ../demo.scala */

println("ScalaLi demo")
import com.dragade.scalali.ScalaLi
import java.util.Scanner

val apiKey = ""
val secretKey = ""

val scalali = new ScalaLi(apiKey, secretKey)
val (url,requestToken, requestSecret) = scalali.initialize()
println("Go hit URL %s and then come back with the verifier code.".format(url))
print("Verifier: ")

val verifier = (new Scanner(System.in)).next
println()
println("Using verifier " + verifier)
val (accessToken,accessSecret) = scalali.verify(requestToken,requestSecret,verifier)

println("Fetching your profile")
val myProfile = scalali.myProfile(accessToken,accessSecret)
