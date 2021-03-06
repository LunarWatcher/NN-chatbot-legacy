package io.github.lunarwatcher.chatbot.bot.commands

import io.github.lunarwatcher.chatbot.bot.chat.BMessage
import io.github.lunarwatcher.chatbot.bot.sites.Chat
import io.github.lunarwatcher.chatbot.utils.Utils
import java.util.*
import javax.script.ScriptEngineManager

@Suppress("NAME_SHADOWING")
class RandomNumber : AbstractCommand("random", listOf("dice"), "Generates a random number"){
    val random: Random = Random(System.currentTimeMillis());

    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input)){
            return null;
        }
        try {
            val split = input.split(" ");
            when {
                split.size == 1 -> return BMessage(randomNumber(6, 1), true)
                split.size == 2 -> return BMessage(randomNumber(split[1].toInt(), 1), true)
                split.size >= 3 -> return BMessage(randomNumber(split[1].toInt(), split[2].toInt()), true)
                else -> {
                }
            }
        }catch(e: Exception){
            return BMessage("Something went terribly wrong", true);
        }
        return BMessage("You shouldn't see this", true);
    }

    fun randomNumber(limit: Int, count: Int): String{
        val count = if(count > 500) 500 else count;
        val builder = StringBuilder()
        for(i in 0 until count){
            builder.append((if(i == count - 1) random.nextInt(limit) else random.nextInt(limit).toString() + ", "))
        }
        return builder.toString();
    }
}


const val GOOGLE_LINK = "https://www.google.com/search?q=";
const val DDG_LINK = "https://www.duckduckgo.com/?q=";

class LMGTFY : AbstractCommand("lmgtfy", listOf("searchfor", "google"), "Sends a link to Google in chat"){
    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input)){
            return null;
        }

        var query = removeName(input).trim()
        if(query.isEmpty())
            return BMessage("You have to supply a query", true);

        if(input.contains("--ddg") || input.contains("--duckduckgo")){
            query = query.replace("--ddg", "").replace("--duckduckgo", "").trim()
            if(query.isEmpty())
                return BMessage("You have to supply a query", true);

            query = query.replace(" ", "+")
            return BMessage("$DDG_LINK$query", false)
        }

        query = query.replace(" ", "+")

        return BMessage("$GOOGLE_LINK$query", false)
    }
}


class Kill(val chat: Chat) : AbstractCommand("kill", listOf("assassinate"), "They must be disposed of!"){

    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input))
            return null;
        val split = splitCommand(input);

        if (split.size < 2 || !split.keys.contains("content")){
            return BMessage("You have to tell me who to dispose of", true);
        }
        val name: String = split["content"] ?: return BMessage("You have to tell me who to dispose of", true);

        if(chat.name == "discord"){
            if(name.toLowerCase().contains("<@!" + chat.site.config.userID + ">")){
                return BMessage("I'm not killing myself.", true);
            }
        }else{
            if(name.toLowerCase().contains(("@" + chat.site.config.username).toLowerCase())){
                return BMessage("I'm not killing myself", true);
            }
        }

        return BMessage(Utils.getRandomKillMessage(name), true);
    }
}

class Lick(val chat: Chat) : AbstractCommand("lick", listOf(), "Licks someone. Or something"){
    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input))
            return null;
        val split = splitCommand(input);
        if (split.size < 2 || !split.keys.contains("content")){
            return BMessage("You have to tell me who to lick", true);
        }

        val name: String = split["content"] ?: return BMessage("You have to tell me who to lick", true);

        return BMessage(Utils.getRandomLickMessage(name), true);
    }
}

class Give(val chat: Chat) : AbstractCommand("give", listOf(), "Gives someone something"){

    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input)){
            return null;
        }
        val inp = splitCommand(input);
        val split = inp["content"]?.split(" ", limit=2) ?: return BMessage("You have to tell me what to give and to who", false);
        if(split.size != 2)
            return BMessage("You have to tell me what to give and to who", true);

        val who = split[0].trim()
        val what = split[1].trim()
        return BMessage("*gives $what to $who*", false);

    }
}

class Ping : AbstractCommand("ping", listOf("poke"), "Pokes someone"){
    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input)) return null;

        val inp = splitCommand(input);
        var content = inp["content"]?.replace(" ", "") ?: return null;
        if(!content.startsWith("@"))
            content = "@$content";
        return BMessage("*pings $content*", false);
    }
}

class Blame(val site: Chat) : AbstractCommand("blame", listOf(), help="Someone must be blamed for this!"){
    private val random = Random();

    override fun handleCommand(input: String, user: User): BMessage? {
        val problem = splitCommand(input)["content"]

        val blamable = site.config.ranks.values.map{it.username}.filter{it != null}
        return if(problem == null)
            BMessage("It is ${blamable[random.nextInt(blamable.size)]}'s fault!", true);
        else BMessage("blames ${blamable[random.nextInt(blamable.size)]} for $problem", true)
    }
}

class JSEval : AbstractCommand("eval", listOf(), desc="Calls the ScriptEngineManager to evaluate JS. Also does basic maths."){
    private val engineManager = ScriptEngineManager()
    private val engine = engineManager.getEngineByName("javascript")

    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input))
            return null

        val content = splitCommand(input)["content"]?.trim() ?: return BMessage("You have to tell me what to evaluate", true)
        return try{
            BMessage(engine.eval(content)?.toString(), true)
        }catch(e: Throwable){
            e.printStackTrace()
            BMessage(e.toString(), true)
        }
    }
}

class StockComments(val site: Chat) {
    //TODO allow creation of stock comments for specific rooms saved in the dataabase
    //Also, this commit is a part in testing some doge aliases for git :3

}

class Appul : AbstractCommand("appul", listOf("apple"), "Apples."){
    override fun handleCommand(input: String, user: User): BMessage? {
        if(!matchesCommand(input)) return null;
        return BMessage("I LUV APPULS!", true);
    }
}