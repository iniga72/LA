package LuckyAuthServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;



import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.google.common.net.HttpHeaders.USER_AGENT;


public abstract class VkApi {
    private final BukkitRunnable run;
    private String server = null;
    private String key;
    public static String keyboard;
    private int ts;
    private String apikey;
    private String id;
    private HashSet<SendingMessage> buffer = new HashSet<>();
    private boolean create;
    private int error = 0;
    private JavaPlugin plugin;
    private boolean destroyed = false;


    public VkApi(JavaPlugin plugin, int speed, String apikey, String id) {
        this.apikey = apikey;
        this.id = id;
        getLongPool();
        this.create = check() == 0;
        this.plugin = plugin;
        this.run = startLongPool(speed);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
        run.cancel();
    }

    private void getLongPool() {
        JsonObject json = Objects.requireNonNull(vkCall("groups.getLongPollServer", "group_id", this.id)).getAsJsonObject("response");
        key = json.get("key").getAsString();
        server = json.get("server").getAsString();
        ts = json.get("ts").getAsInt();
    }

    private int check() {

        return error;
    }

    public boolean checkCreate() {
        return create;
    }

    private JsonObject vkCall(String method, String... arrStr) {
        HashMap<String, String> data = new HashMap<>();
        String v = "5.85";
        data.put("v", v);
        data.put("access_token", this.apikey);
        String str1 = null;
        boolean bol = true;
        for (String str : arrStr) {
            if (bol) {
                str1 = str;
                bol = false;
            } else {
                data.put(str1, str);
                bol = true;
            }
        }
        //String request = HttpRequest.post(API_URL + method).form(data).body();
        String API_URL = "https://api.vk.com/method/";
        String request = Http.sendPost(API_URL + method, data);
        if (request == null)
            return null;
        JsonObject json = new JsonParser().parse(request).getAsJsonObject();
        if (json.has("error"))
            this.error = json.getAsJsonObject("error").get("error_code").getAsInt();
        return json;
    }

    public void sendMessage(SendingMessage message, boolean directly) {
        if (directly) {
            vkCall("messages.send", "peer_id", message.getPeer(), "message", message.toString(), "" );
        } else {
            buffer.add(message);
        }
    }

    public void sendMessage(SendingMessage message) {
        sendMessage(message, false);
    }

    public void sendMessage(String message, String from, String keyboard1) {
        keyboard = keyboard1;
        sendMessage(new SendingMessage(message, from));
    }
    private LinkedHashSet<ReceivedMessage> getMessage() {
        LinkedHashSet<ReceivedMessage> list = new LinkedHashSet<>();
        if (server == null)
            return list;
        //СЃРѕСЃС‚Р°РІР»СЏРµРј СѓСЂР» Р·Р°РїСЂРѕСЃР° РёСЃРїРѕР»СЊР·СѓСЏ Р·РЅР°С‡РµРЅРёСЏ РїРѕР»СѓС‡РµРЅРЅС‹Рµ РїСЂРё РІС‹Р·РѕРІРµ РјРµС‚РѕРґР° getLongPoolServer
        String url = server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=0";
        String request = Http.sendPost(url, null);
        JsonObject json = new JsonParser().parse(request).getAsJsonObject();
        //РћР±СЉСЏРІР»СЏРµРј Р»РёСЃС‚ СЃ СЃРѕРѕР±С‰РµРЅРёСЏРјРё
        if (json.has("failed")) {
            //Р¤РёРєСЃРёСЂСѓРµРј РєРѕРґ РѕС€РёР±РєРё РµСЃР»Рё РѕРЅР° РµСЃС‚СЊ
            int error = json.get("failed").getAsInt();
            if (error != 1)
            //Р•СЃР»Рё РѕС€РёР±РєР° РЅРµ СЂР°РІРЅР° 1 С‚Рѕ РЅСѓР¶РЅРѕ РїРѕР»СѓС‡РёС‚СЊ РЅРѕРІС‹Р№ СЃРµСЂРІРµСЂ РїРѕ РґРѕРєСѓРјРµРЅС‚Р°С†РёРё
            {
                getLongPool();
                return list;
            }
        }
        //РћР±РЅРѕРІР»СЏРµРј С‚СЃ(С‚РѕР¶Рµ РїРѕ РґРѕРєСѓРјРµРЅС‚Р°С†РёРё)
        ts = json.get("ts").getAsInt();
        //РњР°СЃСЃРёРІ РѕР±РЅРѕРІР»РµРЅРёР№
        JsonArray update = json.getAsJsonArray("updates");
        //РџСЂРѕРІРµСЂРєРё РЅР° РїСѓСЃС‚РѕС‚Сѓ, РІСЂРѕРґРµ РїРѕРЅСЏС‚РЅР°, РјР°СЃСЃРёРІР° РЅРµС‚ РµСЃР»Рё РµСЃС‚СЊ РѕС€РёР±РєР°, РїРѕСЌС‚РѕРјСѓ РЅР° РІСЃСЏРєРёР№ СЃР»СѓС‡Р°Р№ РµС‰С‘ СЂР°Р· РїСЂРѕРІРµСЂРёРј
        if (update == null || update.size() < 1)
            return list;

        //РџРµСЂРµР±РёСЂР°РµРј РІСЃРёРѕ
        for (int i = 0; i < update.size(); i++) {
            JsonObject arr = update.get(i).getAsJsonObject();
            //Р•СЃР»Рё РќР• РЎРћРћР‘Р©Р•РќР�Р• С‚Рѕ РїСЂРѕРїСѓСЃРєР°РµРј, РёР±Рѕ РЅР°Рј СЌС‚Рѕ РЅРµ РёРЅС‚РµСЂРµСЃРЅРѕ
            if (!arr.get("type").getAsString().equals("message_new"))
                continue;
            JsonObject jobj = arr.getAsJsonObject("object");
            ReceivedMessage in = new Parser(jobj).getMessage();
            list.add(in);

        }
        return list;
    }

    private BukkitRunnable startLongPool(int speed_update) {
        BukkitRunnable run = new BukkitRunnable() {

            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    cancel();
                    return;
                }
                HashSet<SendingMessage> temp = (HashSet<SendingMessage>) buffer.clone();
                buffer.clear();
                HashMap<String, StringBuilder> map = new HashMap<>();
                StringBuilder builder;
                for (SendingMessage message : temp) {
                    if (map.get(message.getPeer()) == null) {
                        builder = new StringBuilder();
                        map.put(message.getPeer(), builder);
                    } else
                        builder = map.get(message.getPeer());
                    builder.append(message.toString()).append("\n");
                }
                map.forEach((p, s) -> vkCall("messages.send", "peer_id", p, "message", s.toString(), "keyboard", keyboard));
                LinkedHashSet<ReceivedMessage> receiveMap = getMessage();;
                for (ReceivedMessage message : receiveMap) {
                    try {
                        receiveMessage(message);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        Bukkit.getLogger().warning("Необходимо указать верный айди и токен группы");
                        cancel();
                    }
                }
            }
        };
        run.runTaskTimerAsynchronously(plugin, 0, speed_update);
        return run;
    }

    protected abstract void receiveMessage(ReceivedMessage message) throws NoSuchAlgorithmException, IOException;

    public String getId() {
        return id;
    }

    private class Parser {
        List<String> ids = new ArrayList<>();
        ReceivedMessage rootMessage;
        private HashMap<String, VkUser> names;

        Parser(JsonObject root) {
            rootMessage = parseMessage(root);
            names = getNames(ids);
            setNames(rootMessage);
        }

        private ReceivedMessage parseMessage(JsonObject rootMsg) {
            String id = Integer.toString(rootMsg.get("from_id").getAsInt());
            if (!ids.contains(id))
                ids.add(id);
            VkUser vkUser = new VkUser(id);
            JsonElement jelement = rootMsg.get("peer_id");
            String peer;
            if (jelement != null) {
                peer = String.valueOf(jelement.getAsInt());
            } else {
                peer = null;
            }
            List<ReceivedMessage> list = new ArrayList<>();
            ReceivedMessage message = new ReceivedMessage(
                    rootMsg.get("text").getAsString(),
                    peer, list, vkUser
            );
            JsonElement fwd = rootMsg.get("fwd_messages");
            if (fwd == null || fwd.getAsJsonArray().size() == 0)
                return message;
            for (JsonElement fwdArrayElement : fwd.getAsJsonArray()) {
                JsonObject fwdObject = fwdArrayElement.getAsJsonObject();
                list.add(parseMessage(fwdObject));
            }
            return message;
        }

        HashMap<String, VkUser> getNames(List<String> ids) {
            HashMap<String, VkUser> result = new HashMap<>();
            List<String> users = new ArrayList<>();
            List<String> groups = new ArrayList<>();
            for (String id : ids) {
                if (id.startsWith("-")) {
                    if (!groups.contains(id)) {
                        groups.add(id);
                    }
                } else {
                    if (!users.contains(id)) {
                        users.add(id);
                    }
                }
            }
            if (users.size() > 0) {
                String usersStr = StringUtils.join(users, ",");
                JsonArray user = vkCall("users.get", "user_ids", usersStr).getAsJsonArray("response");
                for (JsonElement userJson : user) {
                    JsonObject userJsonObj = userJson.getAsJsonObject();
                    String jid = userJsonObj.get("id").getAsString();
                    result.put(jid, new VkUser(jid, userJsonObj.get("first_name").getAsString(), userJsonObj.get("last_name").getAsString()));
                }
            }
            if (groups.size() > 0) {
                String groupStr = StringUtils.join(groups, ",");
                JsonArray group = vkCall("groups.getById", "group_ids", groupStr).getAsJsonArray("response");
                for (JsonElement groupJson : group) {
                    JsonObject groupJsonObj = groupJson.getAsJsonObject();
                    String jid = groupJsonObj.get("id").getAsString();
                    result.put("-" + jid, new VkUser("-" + jid, groupJsonObj.get("name").getAsString()));
                }
            }
            return result;
        }

        private void setNames(ReceivedMessage message) {
            VkUser vk = names.get(message.getUser().getId());
            message.user = vk;
            List<ReceivedMessage> fwd = message.getFwd();
            if (fwd != null)
                fwd.forEach(this::setNames);
        }

        public ReceivedMessage getMessage() {
            return rootMessage;
        }
    }


    public static class ReceivedMessage {
        private List<ReceivedMessage> forward;
        private String msg;
        private String peer;
        private VkUser user;

        private ReceivedMessage(String msg, String peer, List<ReceivedMessage> forward, VkUser user) {
            this.forward = forward;
            this.msg = msg;
            this.user = user;
            this.peer = peer;
        }

        @Override
        public String toString() {
            return getMsg();
        }

        public String getMsg() {
            return msg;
        }

        public String getName() {
            return user.getName();
        }

        public String getId() {
            return user.getId();
        }

        public VkUser getUser() {
            return user;
        }


        public String getPeer() {
            return peer;
        }

        public List<ReceivedMessage> getFwd() {
            return forward;
        }

    }

    public static class SendingMessage {
        private String msg;
        private String peer;

        @Override
        public String toString() {
            return msg;
        }

        public String getPeer() {
            return peer;
        }


        public void setMessage(String msg) {
            this.msg = msg;
        }

        public String getMessage() {
            return msg;
        }

        public void setPeer(String peer) {
            this.peer = peer;
        }



        public SendingMessage(String msg, String peer) {
            this.msg = msg;
            this.peer = peer;


        }
    }

    private static class Http {
        private static String sendPost(String url, Map<String, String> list) {
            try {
                URL obj = new URL(url);
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                if (list != null) {
                    String urlParameters = urlEncode(list);
                    wr.writeBytes(urlParameters);
                }
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                return response.toString();
            } catch (Exception ex) {
                return null;
            }
        }

        static String urlEncode(Map<?, ?> map) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s",
                        urlEncodeUTF8(entry.getKey().toString()),
                        urlEncodeUTF8(entry.getValue().toString())
                ));
            }
            return sb.toString();
        }

        private static String urlEncodeUTF8(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    public static class VkUser {
        private String id;
        private String firstName;
        private String lastName;
        private MessageType type;

        public VkUser(String id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.type = MessageType.user;
        }

        public VkUser(String id, String name) {
            this.id = id;
            this.firstName = name;
            this.type = MessageType.club;
            this.lastName = "";
        }

        @Override
        public String toString() {
            return "[" + getId() + "|" + getFirstName() + " " + getLastName() + "]";
        }

        public VkUser(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getName() {
            return firstName + " " + lastName;
        }

        public MessageType getType() {
            return type;
        }


        public enum MessageType {
            user, club
        }
    }
}
