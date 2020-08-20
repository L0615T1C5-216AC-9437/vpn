package vpn;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static vpn.byteCode.*;


public class Main extends Plugin {
    public static JSONObject vpn = null;

    public Main() {
        Events.on(EventType.ServerLoadEvent.class, event-> {
            if (!hasDir("mind_db/vpn")) mkdir("mind_db/vpn");
            if (!has("vpn")) make("vpn", new JSONObject());
            if (!has("index")) make("index", new JSONObject());
            vpn = get("vpn");
            if (vpn == null) {
                Log.err(System.getProperty("user.home")+"/mind_db/vpn/vpn.cn is a invalid JSON.");
                Log.info("Go Here to Validate your JSON File: https://codebeautify.org/jsonviewer");
                return;
            }
            JSONObject index = get("index");
            if (index == null) {
                Log.err(System.getProperty("user.home")+"/mind_db/vpn/index.cn is a invalid JSON.");
                Log.info("Go Here to Validate your JSON File: https://codebeautify.org/jsonviewer");
            } else {
                if (!index.has("date")) {
                    LocalDate thisDate = LocalDate.now();
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM");
                    putStr("index", "date", dateFormat.format(thisDate));
                }
            }
            if (!vpn.has("threshold")) putInt("vpn", "threshold", 20);
            if (!vpn.has("kickMessage")) putStr("vpn", "kickMessage", "VPN Detected! - VPN are allowed in this server");
            if (!vpn.has("threshold") || !vpn.has("kickMessage")) vpn = get("vpn");

            Log.info("VPN Cache stored in "+System.getProperty("user.home")+"/mind_db/vpn/");
            Log.info("Configure Anti-VPN by modifying the vpn.cn file. Then Restart Server.");
        });
        Events.on(EventType.PlayerJoin.class, event-> {
            Player player = event.player;
            if (vpn != null) {
                if (has(player.con.address)) {
                    JSONObject data = get(player.con.address);
                    if (!data.has(player.uuid)) {
                        data.put(player.uuid, 0);
                        save(player.con.address, data);
                    }
                    if (data.keySet().size() > vpn.getInt("threshold")) {
                        if (!data.has("vpn")) {
                            data.put("vpn", true);
                            save(player.con.address, data);
                            Log.warn(player.con.address + " flagged as vpn.");
                        } else {
                            if (data.getBoolean("vpn")) Call.onKick(player.con, vpn.getString("kickMessage"));
                        }
                    }
                } else {
                    make(player.con.address, new JSONObject());
                    putInt(player.con.address, player.uuid, 0);
                    putInt("index", player.con.address, 0);
                }
            }
        });
        Events.on(EventType.GameOverEvent.class, gameOverEvent -> {
            LocalDate thisDate = LocalDate.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM");
            JSONObject index = get("index");

            if (index == null) {
                Log.err(System.getProperty("user.home")+"/mind_db/vpn/index.cn is a invalid JSON.");
                Log.info("Go Here to Validate your JSON File: https://codebeautify.org/jsonviewer");
                return;
            }

            if (index.has("date") && !index.getString("date").equals(dateFormat.format(thisDate))) {
                putStr("index", "date", dateFormat.format(thisDate));
                for (String ip : index.keySet()) {
                    if (ip.equals("date")) continue;

                    JSONObject data = get(ip);
                    if (data == null || (data.keySet().size() < vpn.getInt("threshold"))) {
                        save(ip, new JSONObject());
                    }
                }
            }
        });
    }
    public void registerServerCommands(CommandHandler handler) {
        handler.register("vpn-clearCache", "Clears IP Cache but keeps flagged VPNs", arg ->  {
            LocalDate thisDate = LocalDate.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM");
            JSONObject index = get("index");
            int VPNs = 0;
            int IPs = 0;

            if (index == null) {
                Log.err(System.getProperty("user.home")+"/mind_db/vpn/index.cn is a invalid JSON.");
                Log.info("Go Here to Validate your JSON File: https://codebeautify.org/jsonviewer");
                return;
            }

            putStr("index", "date", dateFormat.format(thisDate));
            for (String ip : index.keySet()) {
                if (ip.equals("date")) continue;

                JSONObject data = get(ip);
                if (data == null || (data.keySet().size() < vpn.getInt("threshold"))) {
                    save(ip, new JSONObject());
                    VPNs++;
                    continue;
                }
                IPs++;
            }

            Log.info("Cleared Cache. Total of "+IPs+" IPs Cleared and "+VPNs+" VPNs ignored.");
        });
        handler.register("vpn-clearCache", "Prints Cache Directory", arg ->  {
            Log.info("VPN Cache stored in "+System.getProperty("user.home")+"/mind_db/vpn/");
        });
        handler.register("vpn-pardon", "<IP>", "un-Flags IP as VPN permanently.", arg ->  {
            if (has(arg[0])) {
                JSONObject data = get(arg[0]);
                if (data == null) {
                    Log.err(System.getProperty("user.home")+"/mind_db/vpn/"+arg[0]+".cn is a invalid JSON.");
                    Log.info("Go Here to Validate your JSON File: https://codebeautify.org/jsonviewer");
                    return;
                }
                if (data.has("vpn")) {
                    if (data.getBoolean("vpn")) {
                        data.put("vpn", false);
                        save(arg[0], data);
                        Log.info(arg[0]+" un-flagged as VPN. This change is Permanent.");
                    } else {
                        Log.err("IP not flagged as VPN!");
                    }
                } else {
                    Log.err("IP not flagged as VPN!");
                }
            } else {
                Log.err("No ip found in database : "+arg[0]);
            }
        });
    }
}