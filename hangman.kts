//region I/O
object input{
    infix fun text(prompt:String):String{
        output text prompt
        return readLine()?:""
    }
}
object output{
    infix fun text(s:String){
        print(s)
    }
}
//endregion

//region String Transforms and validations
infix fun String.transform(l:(Char) -> Char):String{
    val result = Array<Char>(this.length) { ' '}
    for (i in 0..this.length-1){
        result[i] = l(this[i])
    }
    return result.joinToString("")
}
infix fun Char.hideCharsNotInString(knownChars:String):Char{
    if (knownChars.contains(this)) return this else return '_'
}
val singleLetterRegex = "[aA-zZ]".toRegex()
infix fun String.isSingleLetterNonNullString(OnFail:()->Unit):Boolean {
    val r = singleLetterRegex.matchEntire(this) != null
    if (r == false) {
        OnFail()
    }
    return r
}
typealias StringReturningFunction = () -> String
infix fun StringReturningFunction.whileNotTrue(condition:(String)->Boolean):String{
    var r:String
    do{
        r = this.invoke()
    } while (condition(r) == false)
    return r
}
//endregion

//region Game Data and Conditions
class Game(var word:String, var lives:Int,var chosenChars:String ="")

fun Game.run():GameResult{
    return GameResult(turn(this.word, this.lives, this.chosenChars))
}
class GameResult(var score:Int)
fun gameOverConditionsMet(word:String,lives:Int,chosenChars:String):Boolean{
    //if no lives or whole word has been discovered
    return lives == 0 || word.all { chosenChars.contains(it) }
}
//endregion

tailrec fun turn(word:String, lives:Int, chosenChars:String):Int{

    //Turn
    output text "You have $lives lives.\n"

    //output current secret word replacing unknown letters with underscore '_'
    output text (word transform  { it hideCharsNotInString chosenChars }) + "\n"

    //input an uppercase letter from the player
    val letter = ({ (input text "Insert a letter > ") } whileNotTrue { it isSingleLetterNonNullString
                                                      { output text "It has to be a single letter\n" }})
        .toUpperCase()

    //compute updated game state
    val new_chosenChars = chosenChars + letter
    val new_lives:Int = if (word.contains(letter)) { lives } else { lives -1 }

    //break recursion returning game result when over conditions are met
    when { gameOverConditionsMet(word,new_lives,new_chosenChars) -> return new_lives }

    //next turn *tailrec
    return turn(word, new_lives, new_chosenChars)
}

with(Game(word="KOTLIN",lives = 10).run()){
        when{
            this.score == 0 -> output text "Game Over!"
            this.score > 0  -> output text "You Win! score:${this.score}"
        }
   }

