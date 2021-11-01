package dev.mruniverse.nohackclient.listeners.security;

import dev.mruniverse.nohackclient.Controller;
import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.listeners.Alerts;
import dev.mruniverse.nohackclient.listeners.common.Sanction;
import dev.mruniverse.nohackclient.storage.FileSaveMode;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static dev.mruniverse.nohackclient.command.MainCommand.sendMessage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

public class SecurityController {

    private final NoHackClient plugin;

    private boolean proxyCheck;

    private boolean compromised;

    private boolean qualityCheck;

    private String proxyKey;

    private String qualityKey;

    private String mode;

    private int fraudScore = 50;

    public SecurityController(NoHackClient plugin) {
        this.plugin = plugin;
        load();
        VPNListener vpnListener = new VPNListener(plugin, this);
        plugin.getServer().getPluginManager().registerEvents(vpnListener,plugin);
    }

    @SuppressWarnings("unused")
    public void update() {
        load();
    }

    public void load() {
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        fraudScore = configuration.getInt("settings.checks.vpn-and-proxy.min-fraud-score-to-sanction",50);
        compromised = configuration.getBoolean("settings.checks.toggle-compromised-server-check",true);
        proxyCheck = configuration.getBoolean("settings.checks.vpn-and-proxy.proxycheck.toggle",true);
        proxyKey = configuration.getString("settings.checks.vpn-and-proxy.proxycheck.key","INSERT KEY HERE");
        qualityKey = configuration.getString("settings.checks.vpn-and-proxy.ipqualityscore.key","INSERT KEY HERE");
        qualityCheck = configuration.getBoolean("settings.checks.vpn-and-proxy.ipqualityscore.toggle",true);
        mode = configuration.getString("settings.mode","NORMAL");
    }

    public void execute(String ip, Player player) {
        if(proxyKey.equalsIgnoreCase("INSERT KEY HERE") && proxyCheck || qualityKey.equalsIgnoreCase("INSERT KEY HERE") && qualityCheck) {
            plugin.getLogs().error("VPN/PROXY DETECTION KEY DON'T FOUND! PLEASE CHECK YOUR CONFIGURATION");
            return;
        }
        if(proxyCheck) {
            new HttpRequest(plugin, "get", "https://proxycheck.io/v2/" + ip + "?key=" + getKey(Keys.PROXYCHECK) + "&vpn=1",player,ip);
        }
        if(qualityCheck) {
            new HttpRequest(plugin, "get", "https://www.ipqualityscore.com/api/json/ip/" + getKey(Keys.IPQUALITYSCORE) + "/" + ip,player,ip);
        }
    }

    public void debug(boolean all,String ip, String player,Player staff) {
        if(proxyKey.equalsIgnoreCase("INSERT KEY HERE") && proxyCheck || qualityKey.equalsIgnoreCase("INSERT KEY HERE") && qualityCheck) {
            plugin.getLogs().error("VPN/PROXY DETECTION KEY DON'T FOUND! PLEASE CHECK YOUR CONFIGURATION");
            return;
        }
        if(proxyCheck) {
            new HttpDebugRequest(plugin, "get", "https://proxycheck.io/v2/" + ip + "?key=" + getKey(Keys.PROXYCHECK) + "&vpn=1",player,ip,staff,all);
        }
        if(qualityCheck) {
            new HttpDebugRequest(plugin, "get", "https://www.ipqualityscore.com/api/json/ip/" + getKey(Keys.IPQUALITYSCORE) + "/" + ip,player,ip,staff,all);
        }
    }


    public void checkResult(String ip,Keys key,boolean debugAll, String name,Player staff, HashMap<String,String> result) {
        String format = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debug-format","&a<check>: &6<result>"));
        String results = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.result","Result"));
        String none = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.no-result","&bNo results found."));
        String found = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.found","&cFound"));
        String requestLimit = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.request-limit","&eYou have exceeded your request quota of 200 per day. Please upgrade to increase your request quota."));
        String requestError = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.request-error","&eRequest Issue, check console to see information."));
        if(key == Keys.PROXYCHECK) {
            if(result.get("status").equalsIgnoreCase("ok")) {
                String toConvert = result.get(ip);
                try {
                    JSONObject object = (JSONObject) new JSONParser().parse(toConvert);
                    sendMessage(staff, "&dResults of " + name);
                    String proxy = object.get("proxy").toString();
                    if (proxy.equalsIgnoreCase("yes")) {
                        sendMessage(staff, "&6ProxyCheck:");
                        String type = object.get("type").toString();
                        String foundIssue = format
                                .replace("<check>", results)
                                .replace("<result>", found);
                        if (type.equalsIgnoreCase("VPN")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1V1")
                            );
                        }
                        if (type.equalsIgnoreCase("TOR")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1T1")
                            );
                        }
                        if (type.equalsIgnoreCase("Shadowsocks")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1SS1")
                            );
                        }
                        if (type.equalsIgnoreCase("HTTP")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1H1")
                            );
                        }
                        if (type.equalsIgnoreCase("HTTPS")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1H2")
                            );
                        }
                        if (type.equalsIgnoreCase("Compromised Server")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1CS1")
                            );
                        }
                        if (type.equalsIgnoreCase("Inference Engine")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1IE1")
                            );
                        }
                        if (type.equalsIgnoreCase("OpenVPN")) {
                            sendMessage(staff, foundIssue
                                    .replace("<code>", "CK1-C1OV1")
                            );
                        }
                    } else {
                        sendMessage(staff, "&6ProxyCheck:");
                        sendMessage(staff, format
                                .replace("<check>", results)
                                .replace("<result>", none)
                                .replace("<code>", "")
                        );
                    }
                } catch (Throwable throwable) {
                    plugin.getLogs().error(throwable);
                }
                return;
            }
            sendMessage(staff,requestError);
            return;
        }
        int fraud;
        try {
            fraud = Integer.parseInt(result.get("fraud_score"));
        }catch (Throwable ignored) {
            fraud = 0;
        }
        sendMessage(staff,format
                .replace("<check>","Fraud Score")
                .replace("<result>",fraud + " (Max: " + fraudScore + ")")
        );
        if(!debugAll) return;
        sendMessage(staff,"&6IpQualityScore:");
        if(result.get("success").equalsIgnoreCase("false")) {
            sendMessage(staff,requestLimit);
            return;
        }
        if(result.get("proxy").equalsIgnoreCase("true")) {
            sendMessage(staff,format
                    .replace("<check>","Proxy")
                    .replace("<result>",found)
                    .replace("<code>","CK2-C2CS2")
            );
        } else {
            sendMessage(staff,format
                    .replace("<check>","Proxy")
                    .replace("<result>",none)
                    .replace("<code>","")
            );
        }
        if(result.get("active_tor").equalsIgnoreCase("true")) {
            sendMessage(staff,format
                    .replace("<check>","Active TOR")
                    .replace("<result>",found)
                    .replace("<code>","CK2-C2T2")
            );
        } else {
            sendMessage(staff,format
                    .replace("<check>","Active TOR")
                    .replace("<result>",none)
                    .replace("<code>","")
            );
        }
        if(result.get("tor").equalsIgnoreCase("true")) {
            sendMessage(staff,format
                    .replace("<check>","TOR")
                    .replace("<result>",found)
                    .replace("<code>","CK2-C2T1")
            );
        } else {
            sendMessage(staff,format
                    .replace("<check>","TOR")
                    .replace("<result>",none)
                    .replace("<code>","")
            );
        }
        if(result.get("active_vpn").equalsIgnoreCase("true")) {
            sendMessage(staff,format
                    .replace("<check>","ActiveVPN")
                    .replace("<result>",found)
                    .replace("<code>","CK2-C2V2")
            );
        } else {
            sendMessage(staff,format
                    .replace("<check>","ActiveVPN")
                    .replace("<result>",none)
                    .replace("<code>","")
            );
        }
        if(result.get("vpn").equalsIgnoreCase("true")) {
            sendMessage(staff,format
                    .replace("<check>","VPN")
                    .replace("<result>",found)
                    .replace("<code>","CK2-C2V1")
            );
        } else {
            sendMessage(staff,format
                    .replace("<check>","VPN")
                    .replace("<result>",none)
                    .replace("<code>","")
            );
        }
    }


    public void result(String ip,Keys key, Player player, HashMap<String,String> result) {
        String name = player.getName();
        Controller controller = plugin.getController();
        String requestLimit = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.request-limit","&eYou have exceeded your request quota of 200 per day. Please upgrade to increase your request quota."));
        String requestError = ChatColor.translateAlternateColorCodes('&', plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.debugs.request-error","&eRequest Issue, check console to see information."));
        if(key == Keys.PROXYCHECK) {
            if(result.get("status").equalsIgnoreCase("ok")) {
                String toConvert = result.get(ip);
                try {
                    JSONObject object = (JSONObject) new JSONParser().parse(toConvert);
                    String proxy = object.get("proxy").toString();
                    if (proxy.equalsIgnoreCase("yes")) {
                        String type = object.get("type").toString();
                        if (type.equalsIgnoreCase("VPN")) {
                            controller.sendAlert(Alerts.VPN, name);
                            executeBan(Alerts.VPN, player, "CK1", "C1V1");
                            return;
                        }
                        if (type.equalsIgnoreCase("TOR")) {
                            controller.sendAlert(Alerts.TOR, name);
                            executeBan(Alerts.TOR, player, "CK1", "C1T1");
                            return;
                        }
                        if (type.equalsIgnoreCase("Shadowsocks")) {
                            controller.sendAlert(Alerts.SHADOW_SOCKS, name);
                            executeBan(Alerts.SHADOW_SOCKS, player, "CK1", "C1SS1");
                            return;
                        }
                        if (type.equalsIgnoreCase("HTTP")) {
                            controller.sendAlert(Alerts.HTTP, name);
                            executeBan(Alerts.HTTP, player, "CK1", "C1H1");
                            return;
                        }
                        if (type.equalsIgnoreCase("HTTPS")) {
                            controller.sendAlert(Alerts.HTTPS, name);
                            executeBan(Alerts.HTTPS, player, "CK1", "C1H2");
                            return;
                        }
                        if (compromised) {
                            if (type.equalsIgnoreCase("Compromised Server")) {
                                controller.sendAlert(Alerts.COMPROMISED_SERVER, name);
                                executeBan(Alerts.COMPROMISED_SERVER, player, "CK1", "C1CS1");
                                return;
                            }
                        }
                        if (type.equalsIgnoreCase("Inference Engine")) {
                            controller.sendAlert(Alerts.INFERENCE_ENGINE, name);
                            executeBan(Alerts.INFERENCE_ENGINE, player, "CK1", "C1IE1");
                            return;
                        }
                        if (type.equalsIgnoreCase("OpenVPN")) {
                            controller.sendAlert(Alerts.OPEN_VPN, name);
                            executeBan(Alerts.OPEN_VPN, player, "CK1", "C1OV1");
                            return;
                        }
                    }
                } catch (Throwable throwable) {
                    plugin.getLogs().error(throwable);
                }
                return;
            }
            plugin.getLogs().error("&6PROXYCHECK: &f" + requestError);
            return;
        }
        int fraud;
        try {
            fraud = Integer.parseInt(result.get("fraud_score"));
        }catch (Throwable ignored) {
            fraud = 0;
        }
        plugin.getLogs().info("Fraud Score of " + name + ": " + fraud);
        if(fraud >= fraudScore && fraudScore != -1 && fraudScore != 0) {
            plugin.getLogs().info("Fraud score reached by: " + name);
            plugin.getLogs().info("Max: " + fraudScore + " Tiene: " + fraud);
            controller.sendAlert(Alerts.FRAUD_SCORE,name);
            executeBan(Alerts.FRAUD_SCORE,player,"CK2","C2FS1");
        } else {
            if(mode.equalsIgnoreCase("NORMAL") || mode.equalsIgnoreCase("DEFAULT")) return;
            if(result.get("success").equalsIgnoreCase("false")) {
                plugin.getLogs().info("&6IPQUALITYSCORE: &f" + requestLimit);
                return;
            }
            if(result.get("proxy").equalsIgnoreCase("true")) {
                controller.sendAlert(Alerts.COMPROMISED_SERVER,name);
                executeBan(Alerts.COMPROMISED_SERVER,player,"CK2","C2CS1");
                return;
            }
            if(mode.equalsIgnoreCase("NORMAL_MEDIUM")) return;
            if(result.get("active_tor").equalsIgnoreCase("true")) {
                controller.sendAlert(Alerts.TOR,name);
                executeBan(Alerts.TOR,player,"CK2","C2T2");
                return;
            }
            if(result.get("tor").equalsIgnoreCase("true")) {
                controller.sendAlert(Alerts.TOR,name);
                executeBan(Alerts.TOR,player,"CK2","C2T1");
                return;
            }
            if(mode.equalsIgnoreCase("MEDIUM")) return;
            if(result.get("active_vpn").equalsIgnoreCase("true")) {
                controller.sendAlert(Alerts.VPN,name);
                executeBan(Alerts.VPN,player,"CK2","C2V2");
                return;
            }
            if(result.get("vpn").equalsIgnoreCase("true")) {
                controller.sendAlert(Alerts.VPN,name);
                executeBan(Alerts.VPN,player,"CK2","C2V1");
            }
        }
    }



    private void executeBan(Alerts alert,Player player,String sanctionName,String sanctionID) {
        String name = player.getName();
        if(!plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users").contains(name)) {
            List<String> users = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users");
            users.add(name);
            plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("users",users);
            plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("casesString." + name,alert.getName() + "," + sanctionName + "," + sanctionID);
            try {
                InetSocketAddress socket = player.getAddress();
                if(socket != null) {
                    InetAddress address = socket.getAddress();
                    plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("ips." + name, address.getHostAddress());
                }
            } catch(Throwable ignored) {}
            plugin.getStorage().save(FileSaveMode.PLAYERS);
            plugin.getStorage().reloadFile(FileSaveMode.PLAYERS);
        }
        Sanction sanction = new Sanction(sanctionName,sanctionID);
        BukkitRunnable runnable = new SanctionRunnable(plugin,player,alert,sanction);
        runnable.runTaskLater(plugin,80);
    }

    public String getKey(Keys key) {
        if(key == Keys.PROXYCHECK) {
            return proxyKey;
        }
        return qualityKey;
    }

    public enum Keys {
        PROXYCHECK,
        IPQUALITYSCORE
    }
}
