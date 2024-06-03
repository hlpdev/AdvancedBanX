package net.hnt8.advancedban.utils;

import net.hnt8.advancedban.MethodInterface;
import net.hnt8.advancedban.Universal;
import net.hnt8.advancedban.manager.DatabaseManager;
import net.hnt8.advancedban.manager.MessageManager;
import net.hnt8.advancedban.manager.PunishmentManager;
import net.hnt8.advancedban.manager.UUIDManager;
import net.hnt8.advancedban.utils.commands.ListProcessor;
import net.hnt8.advancedban.utils.commands.PunishmentProcessor;
import net.hnt8.advancedban.utils.commands.RevokeByIdProcessor;
import net.hnt8.advancedban.utils.commands.RevokeProcessor;
import net.hnt8.advancedban.utils.tabcompletion.BasicTabCompleter;
import net.hnt8.advancedban.utils.tabcompletion.CleanTabCompleter;
import net.hnt8.advancedban.utils.tabcompletion.PunishmentTabCompleter;
import net.hnt8.advancedban.utils.tabcompletion.TabCompleter;
import net.hnt8.advancedban.utils.tabcompletion.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.hnt8.advancedban.utils.CommandUtils.*;

public enum Command {
    BAN(
            PunishmentType.BAN.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.BAN),
            PunishmentType.BAN.getConfSection("Usage"),
            "ban"),

    TEMP_BAN(
            PunishmentType.TEMP_BAN.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_BAN),
            PunishmentType.TEMP_BAN.getConfSection("Usage"),
            "tempban"),

    IP_BAN(
            PunishmentType.IP_BAN.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.IP_BAN),
            PunishmentType.IP_BAN.getConfSection("Usage"),
            "ipban", "banip", "ban-ip"),

    TEMP_IP_BAN(
            PunishmentType.TEMP_IP_BAN.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_IP_BAN),
            PunishmentType.TEMP_IP_BAN.getConfSection("Usage"),
            "tempipban"),

    MUTE(
            PunishmentType.MUTE.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.MUTE),
            PunishmentType.MUTE.getConfSection("Usage"),
            "mute"),

    TEMP_MUTE(
            PunishmentType.TEMP_MUTE.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_MUTE),
            PunishmentType.TEMP_MUTE.getConfSection("Usage"),
            "tempmute"),

    WARN(
            PunishmentType.WARNING.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.WARNING),
            PunishmentType.WARNING.getConfSection("Usage"),
            "warn"),

    TEMP_WARN(
            PunishmentType.TEMP_WARNING.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_WARNING),
            PunishmentType.TEMP_WARNING.getConfSection("Usage"),
            "tempwarn"),

    NOTE(
            PunishmentType.NOTE.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.NOTE),
            PunishmentType.NOTE.getConfSection("Usage"),
            "note"),

    KICK(
            PunishmentType.KICK.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            input -> {
                if (!Universal.get().getMethods().isOnline(input.getPrimaryData())) {
                    MessageManager.sendMessage(input.getSender(), "Kick.NotOnline", true,
                            "NAME", input.getPrimary());
                    return;
                }

                new PunishmentProcessor(PunishmentType.KICK).accept(input);
            },
            PunishmentType.KICK.getConfSection("Usage"),
            "kick"),

    UN_BAN("ab." + PunishmentType.BAN.getName() + ".undo",
            "\\S+",
            new BasicTabCompleter("[Name/IP]"),
            new RevokeProcessor(PunishmentType.BAN),
            "Un" + PunishmentType.BAN.getConfSection("Usage"),
            "unban"),

    UN_MUTE("ab." + PunishmentType.MUTE.getName() + ".undo",
            "\\S+",
            new BasicTabCompleter(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]"),
            new RevokeProcessor(PunishmentType.MUTE),
            "Un" + PunishmentType.MUTE.getConfSection("Usage"),
            "unmute"),

    UN_WARN("ab." + PunishmentType.WARNING.getName() + ".undo",
            "[0-9]+|(?i:clear \\S+)",
            new CleanTabCompleter((user, args) -> {
                if(args.length == 1) {
                    return MutableTabCompleter.list("[ID]", "clear");
                }else if(args.length == 2 && args[0].equalsIgnoreCase("clear")){
                    return MutableTabCompleter.list(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]");
                } else {
                    return MutableTabCompleter.list();
                }
            }),
            input -> {
                final String confSection = PunishmentType.WARNING.getName();
                if (input.getPrimaryData().equals("clear")) {
                    input.next();
                    String name = input.getPrimary();
                    String uuid = processName(input);
                    if (uuid == null)
                        return;

                    List<Punishment> punishments = PunishmentManager.get().getWarns(uuid);
                    if (punishments.isEmpty()) {
                        MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Empty",
                                true, "NAME", name);
                        return;
                    }

                    String operator = Universal.get().getMethods().getName(input.getSender());
                    for (Punishment punishment : punishments) {
                        punishment.delete(operator, true, true);
                    }
                    MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Done",
                            true, "COUNT", String.valueOf(punishments.size()));
                } else {
                    new RevokeByIdProcessor("Un" + confSection, PunishmentManager.get()::getWarn).accept(input);
                }
            },
            "Un" + PunishmentType.WARNING.getConfSection("Usage"),
            "unwarn"),
    UN_NOTE("ab." + PunishmentType.NOTE.getName() + ".undo",
            "[0-9]+|(?i:clear \\S+)",
            new CleanTabCompleter((user, args) -> {
                if(args.length == 1) {
                    return MutableTabCompleter.list("[ID]", "clear");
                }else if(args.length == 2 && args[0].equalsIgnoreCase("clear")){
                    return MutableTabCompleter.list(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]");
                } else {
                    return MutableTabCompleter.list();
                }
            }),
            input -> {
                final String confSection = PunishmentType.NOTE.getName();
                if (input.getPrimaryData().equals("clear")) {
                    input.next();
                    String name = input.getPrimary();
                    String uuid = processName(input);
                    if (uuid == null)
                        return;

                    List<Punishment> punishments = PunishmentManager.get().getNotes(uuid);
                    if (punishments.isEmpty()) {
                        MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Empty",
                                true, "NAME", name);
                        return;
                    }

                    String operator = Universal.get().getMethods().getName(input.getSender());
                    for (Punishment punishment : punishments) {
                        punishment.delete(operator, true, true);
                    }
                    MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Done",
                            true, "COUNT", String.valueOf(punishments.size()));
                } else {
                    new RevokeByIdProcessor("Un" + confSection, PunishmentManager.get()::getNote).accept(input);
                }
            },
            "Un" + PunishmentType.NOTE.getConfSection("Usage"),
            "unnote"),

    UN_PUNISH("ab.all.undo",
            "[0-9]+",
            new BasicTabCompleter("<ID>"),
            new RevokeByIdProcessor("UnPunish", PunishmentManager.get()::getPunishment),
            "UnPunish.Usage",
            "unpunish"),

    CHANGE_REASON("ab.changeReason",
            "([0-9]+|(?i)(ban|mute) \\S+) .+",
            new CleanTabCompleter((user, args) -> {
                if(args.length <= 1) {
                    return MutableTabCompleter.list("<ID>", "ban", "mute");
                }else {
                    boolean playerTarget = args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("mute");
                    if(args.length == 2 && playerTarget){
                        return MutableTabCompleter.list(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]");
                    } else if((playerTarget && args.length == 3) || args.length == 2){
                        return MutableTabCompleter.list("new reason...");
                    } else {
                        return MutableTabCompleter.list();
                    }
                }
            }),
            input -> {
                Punishment punishment;

                if (input.getPrimaryData().matches("[0-9]*")) {
                    int id = Integer.parseInt(input.getPrimaryData());
                    input.next();

                    punishment = PunishmentManager.get().getPunishment(id);
                } else {
                    PunishmentType type = PunishmentType.valueOf(input.getPrimary().toUpperCase());
                    input.next();

                    String target = input.getPrimary();
                    if (!target.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                        target = processName(input);
                        if (target == null)
                            return;
                    } else {
                        input.next();
                    }

                    punishment = getPunishment(target, type);
                }

                String reason = processReason(input);
                if (reason == null)
                    return;

                if (punishment != null) {
                    punishment.updateReason(reason);
                    MessageManager.sendMessage(input.getSender(), "ChangeReason.Done",
                            true, "ID", String.valueOf(punishment.getId()));
                } else {
                    MessageManager.sendMessage(input.getSender(), "ChangeReason.NotFound", true);
                }
            },
            "ChangeReason.Usage",
            "change-reason"),

    BAN_LIST("ab.banlist",
            "([1-9][0-9]*)?",
            new BasicTabCompleter("<Page>"),
            new ListProcessor(
                    target -> PunishmentManager.get().getPunishments(SQLQuery.SELECT_ALL_PUNISHMENTS_LIMIT, 150),
                    "Banlist", false, false),
            "Banlist.Usage",
            "banlist"),

    HISTORY("ab.history",
            "\\S+( [1-9][0-9]*)?",
            new CleanTabCompleter((user, args) -> {
                if(args.length == 1)
                    return MutableTabCompleter.list(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]");
                else if(args.length == 2)
                    return MutableTabCompleter.list("<Page>");
                else
                    return MutableTabCompleter.list();
            }),
            new ListProcessor(
                    target -> PunishmentManager.get().getPunishments(target, null, false),
                    "History", true, true),
            "History.Usage",
            "history"),

    WARNS(null,
            "\\S+( [1-9][0-9]*)?|\\S+|",
            new CleanTabCompleter((user, args) -> {
                if(args.length == 1)
                    if(Universal.get().getMethods().hasPerms(user, "ab.notes.other"))
                        return MutableTabCompleter.list(CleanTabCompleter.PLAYER_PLACEHOLDER, "<Name>", "<Page>");
                    else
                        return MutableTabCompleter.list("<Page>");
                else if(args.length == 2 && !args[0].matches("\\d+"))
                    return MutableTabCompleter.list("<Page>");
                else
                    return MutableTabCompleter.list();
            }),
            input -> {
                if (input.hasNext() && !input.getPrimary().matches("[1-9][0-9]*")) {
                    if (!Universal.get().hasPerms(input.getSender(), "ab.warns.other")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }

                    new ListProcessor(
                            target -> PunishmentManager.get().getPunishments(target, PunishmentType.WARNING, true),
                            "Warns", false, true).accept(input);
                } else {
                    if (!Universal.get().hasPerms(input.getSender(), "ab.warns.own")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }

                    String name = Universal.get().getMethods().getName(input.getSender());
                    String identifier = processName(new Command.CommandInput(input.getSender(), new String[]{name}));
                    new ListProcessor(
                            target -> PunishmentManager.get().getPunishments(identifier, PunishmentType.WARNING, true),
                            "WarnsOwn", false, false).accept(input);
                }
            },
            "Warns.Usage",
            "warns"),
    NOTES(null,
            "\\S+( [1-9][0-9]*)?|\\S+|",
            new CleanTabCompleter((user, args) -> {
                if(args.length == 1)
                    if(Universal.get().getMethods().hasPerms(user, "ab.notes.other"))
                        return MutableTabCompleter.list(CleanTabCompleter.PLAYER_PLACEHOLDER, "<Name>", "<Page>");
                    else
                        return MutableTabCompleter.list("<Page>");
                else if(args.length == 2 && !args[0].matches("\\d+"))
                    return MutableTabCompleter.list("<Page>");
                else
                    return MutableTabCompleter.list();
            }),
            input -> {
                if (input.hasNext() && !input.getPrimary().matches("[1-9][0-9]*")) {
                    if (!Universal.get().hasPerms(input.getSender(), "ab.notes.other")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }

                    new ListProcessor(
                            target -> PunishmentManager.get().getPunishments(target, PunishmentType.NOTE, true),
                            "Notes", false, true).accept(input);
                } else {
                    if (!Universal.get().hasPerms(input.getSender(), "ab.notes.own")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }

                    String name = Universal.get().getMethods().getName(input.getSender());
                    String identifier = processName(new Command.CommandInput(input.getSender(), new String[]{name}));
                    new ListProcessor(
                            target -> PunishmentManager.get().getPunishments(identifier, PunishmentType.NOTE, true),
                            "NotesOwn", false, false).accept(input);
                }
            },
            "Notes.Usage",
            "notes"),

    CHECK("ab.check",
            "\\S+",
            new BasicTabCompleter(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]"),
            input -> {
                String name = input.getPrimary();

                String uuid = processName(input);
                if (uuid == null)
                    return;

                String ip = Universal.get().getIps().getOrDefault(name.toLowerCase(), "none cashed");
                String loc = Universal.get().getMethods().getFromUrlJson("http://ip-api.com/json/" + ip, "country");
                Punishment mute = PunishmentManager.get().getMute(uuid);
                Punishment ban = PunishmentManager.get().getBan(uuid);

                String cached = MessageManager.getMessage("Check.Cached", false);
                String notCached = MessageManager.getMessage("Check.NotCached", false);

                boolean nameCached = PunishmentManager.get().isCached(name.toLowerCase());
                boolean ipCached = PunishmentManager.get().isCached(ip);
                boolean uuidCached = PunishmentManager.get().isCached(uuid);

                Object sender = input.getSender();
                MessageManager.sendMessage(sender, "Check.Header", true, "NAME", name, "CACHED", nameCached ? cached : notCached);
                MessageManager.sendMessage(sender, "Check.UUID", false, "UUID", uuid, "CACHED", uuidCached ? cached : notCached);
                if (Universal.get().hasPerms(sender, "ab.check.ip")) {
                    MessageManager.sendMessage(sender, "Check.IP", false, "IP", ip, "CACHED", ipCached ? cached : notCached);
                }
                MessageManager.sendMessage(sender, "Check.Geo", false, "LOCATION", loc == null ? "failed!" : loc);
                MessageManager.sendMessage(sender, "Check.Mute", false, "DURATION", mute == null ? "<green>none</green>" : mute.getType().isTemp() ? "<yellow>" + mute.getDuration(false) + "</yellow>" : "<red>perma</red>");
                if (mute != null) {
                    MessageManager.sendMessage(sender, "Check.MuteReason", false, "REASON", mute.getReason());
                }
                MessageManager.sendMessage(sender, "Check.Ban", false, "DURATION", ban == null ? "<gree>none</green>" : ban.getType().isTemp() ? "<yellow>" + ban.getDuration(false) + "</yellow>" : "<red>perma</red>");
                if (ban != null) {
                    MessageManager.sendMessage(sender, "Check.BanReason", false, "REASON", ban.getReason());
                }
                MessageManager.sendMessage(sender, "Check.Warn", false, "COUNT", PunishmentManager.get().getCurrentWarns(uuid) + "");

                MessageManager.sendMessage(sender, "Check.Note", false, "COUNT", PunishmentManager.get().getCurrentNotes(uuid) + "");
            },
            "Check.Usage",
            "check"),

    SYSTEM_PREFERENCES("ab.systemprefs",
            ".*",
            null,
            input -> {
                MethodInterface mi = Universal.get().getMethods();
                Calendar calendar = new GregorianCalendar();
                Object sender = input.getSender();
                mi.sendMessage(sender, "<red><bold>AdvancedBanX v3</bold> SystemPrefs</red>");
                mi.sendMessage(sender, "<red>Server-Time</red> <dark_gray>»</dark_gray> <gray>" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + "</gray>");
                mi.sendMessage(sender, "<red>Your UUID (Intern)</red> <dark_gray>»</dark_gray> <gray>" + mi.getInternUUID(sender) + "</gray>");
                if (input.hasNext()) {
                    String target = input.getPrimaryData();
                    mi.sendMessage(sender, "<red>" + target + "'s UUID (Intern)</red> <dark_gray>»</dark_gray> <gray> <gray>" + mi.getInternUUID(target) + "</gray>");
                    mi.sendMessage(sender, "<red>" + target + "'s UUID (Fetched)</red> <dark_gray>»</dark_gray> <gray> <gray>" + UUIDManager.get().getUUID(target) + "</gray>");
                }
            },
            null,
            "systemprefs"),

    ADVANCED_BAN(null,
            ".*",
            new BasicTabCompleter("help", "reload"),
            input -> {
                MethodInterface mi = Universal.get().getMethods();
                Object sender = input.getSender();
                if (input.hasNext()) {
                    if (input.getPrimaryData().equals("reload")) {
                        if (Universal.get().hasPerms(sender, "ab.reload")) {
                            mi.loadFiles();
                            mi.sendMessage(sender, "<green><bold>AdvancedBanX</bold></green> <dark_gray>»</dark_gray> <gray>Reloaded!</gray>");
                        } else {
                            MessageManager.sendMessage(sender, "General.NoPerms", true);
                        }
                        return;
                    } else if (input.getPrimaryData().equals("help")) {
                        if (Universal.get().hasPerms(sender, "ab.help")) {
                            mi.sendMessage(sender, "");
                            mi.sendMessage(sender, "<red><bold>AdvancedBanX</bold></red> <gray>Command-Help</gray>");
                            mi.sendMessage(sender, "");
                            mi.sendMessage(sender, "<red>/ban [Name] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Ban a user permanently</gray>");
                            mi.sendMessage(sender, "<red>/banip [Name/IP] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Ban a user by IP</gray>");
                            mi.sendMessage(sender, "<red>/tempban [Name] [Xmo/Xd/Xh/Xm/Xs/#TimeLayout] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Ban a user temporary</gray>");
                            mi.sendMessage(sender, "<red>/mute [Name] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Mute a user permanently</gray>");
                            mi.sendMessage(sender, "<red>/tempmute [Name] [Xmo/Xd/Xh/Xm/Xs/#TimeLayout] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Mute a user temporary</gray>");
                            mi.sendMessage(sender, "<red>/warn [Name] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Warn a user permanently</gray>");
                            mi.sendMessage(sender, "<red>/note [Name] [Note]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Adds a note to a user</gray>");
                            mi.sendMessage(sender, "<red>/tempwarn [Name] [Xmo/Xd/Xh/Xm/Xs/#TimeLayout] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Warn a user temporary</gray>");
                            mi.sendMessage(sender, "<red>/kick [Name] [Reason/@Layout]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Kick a user</gray>");
                            mi.sendMessage(sender, "<red>/unban [Name/IP]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Unban a user</gray>");
                            mi.sendMessage(sender, "<red>/unmute [Name]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Unmute a user</gray>");
                            mi.sendMessage(sender, "<red>/unwarn [ID] or /unwarn clear [Name]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Deletes a warn</gray>");
                            mi.sendMessage(sender, "<red>/unnote [ID] or /unnote clear [Name]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Deletes a note</gray>");
                            mi.sendMessage(sender, "<red>/change-reason [ID or ban/mute USER] [New reason]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Changes the reason of a punishment</gray>");
                            mi.sendMessage(sender, "<red>/unpunish [ID]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Deletes a punishment by ID</gray>");
                            mi.sendMessage(sender, "<red>/banlist <Page></red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>See all punishments</gray>");
                            mi.sendMessage(sender, "<red>/history [Name/IP] <Page></red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>See a users history</gray>");
                            mi.sendMessage(sender, "<red>/warns [Name] <Page></red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>See your or a users warnings</gray>");
                            mi.sendMessage(sender, "<red>/notes [Name] <Page></red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>See your or a users notes</gray>");
                            mi.sendMessage(sender, "<red>/check [Name]</red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Get all information about a user</gray>");
                            mi.sendMessage(sender, "<red>/AdvancedBan <reload/help></red>");
                            mi.sendMessage(sender, "<dark_gray>»</dark_gray> <gray>Reloads the plugin or shows help page</gray>");
                            mi.sendMessage(sender, "");
                        } else {
                            MessageManager.sendMessage(sender, "General.NoPerms", true);
                        }
                        return;
                    }
                }


                mi.sendMessage(sender, "<bold><dark_gray><strikethrough>-=====</strikethrough></dark_gray> <red>AdvancedBanX v3</red> <dark_gray><strikethrough>=====-</strikethrough></dark_gray></bold>");
                mi.sendMessage(sender, "  <red>Dev</red> <dark_gray>•</dark_gray> <gray>Leoko</gray>");
                mi.sendMessage(sender, "  <red>Maintainer</red> <dark_gray>•</dark_gray> <gray>2vY (hlpdev)</gray>");
                mi.sendMessage(sender, "  <red>Status</red> <dark_gray>•</dark_gray> <green><italic>Stable</italic></green>");
                mi.sendMessage(sender, "  <red>Version</red> <dark_gray>•</dark_gray> <gray>" + mi.getVersion() + "</gray>");
                mi.sendMessage(sender, "  <red>License</red> <dark_gray>•</dark_gray> <gray>Public</gray>");
                mi.sendMessage(sender, "  <red>Storage</red> <dark_gray>•</dark_gray> <gray>" + (DatabaseManager.get().isUseMySQL() ? "MySQL (external)</gray>" : "HSQLDB (local)</gray>"));
                mi.sendMessage(sender, "  <red>Server</red> <dark_gray>•</dark_gray> <gray>" + (Universal.get().isBungee() ? "Bungeecord</gray>" : "Bukkit/Spigot/Paper</gray>"));
                if (Universal.get().isBungee()) {
                    mi.sendMessage(sender, "  <red>RedisBungee</red> <dark_gray>•</dark_gray> <gray>" + (Universal.isRedis() ? "true</gray>" : "false</gray>"));
                }
                mi.sendMessage(sender, "  <red>UUID-Mode</red> <dark_gray>•</dark_gray> <gray>" + UUIDManager.get().getMode() + "</gray>");
                mi.sendMessage(sender, "  <red>Prefix</red> <dark_gray>•</dark_gray> <gray>" + (mi.getBoolean(mi.getConfig(), "Disable Prefix", false) ? "</gray>" : MessageManager.getMessage("General.Prefix") + "</gray>"));
                mi.sendMessage(sender, "<bold><dark_gray><strikethrough>-=========================-</strikethrough></dark_gray></bold>");
            },
            null,
            "advancedban");

    private final String permission;
    private final Predicate<String[]> syntaxValidator;
    private final TabCompleter tabCompleter;
    private final Consumer<CommandInput> commandHandler;
    private final String usagePath;
    private final String[] names;

    Command(String permission, Predicate<String[]> syntaxValidator,
            TabCompleter tabCompleter, Consumer<CommandInput> commandHandler, String usagePath, String... names) {
        this.permission = permission;
        this.syntaxValidator = syntaxValidator;
        this.tabCompleter = tabCompleter;
        this.commandHandler = commandHandler;
        this.usagePath = usagePath;
        this.names = names;
    }

    Command(String permission, String regex, TabCompleter tabCompleter, Consumer<CommandInput> commandHandler,
            String usagePath, String... names) {
        this(permission, (args) -> String.join(" ", args).matches(regex), tabCompleter, commandHandler, usagePath, names);
    }

    public boolean validateArguments(String[] args) {
        return syntaxValidator.test(args);
    }

    public void execute(Object player, String[] args) {
        commandHandler.accept(new CommandInput(player, args));
    }



    public TabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public static Command getByName(String name) {
        String lowerCase = name.toLowerCase();
        for (Command command : values()) {
            for (String s : command.names) {
                if (s.equals(lowerCase))
                    return command;
            }
        }
        return null;
    }

    public String getPermission() {
        return this.permission;
    }

    public Predicate<String[]> getSyntaxValidator() {
        return this.syntaxValidator;
    }

    public Consumer<CommandInput> getCommandHandler() {
        return this.commandHandler;
    }

    public String getUsagePath() {
        return this.usagePath;
    }

    public String[] getNames() {
        return this.names;
    }

    public static class CommandInput {
        private final Object sender;
        private String[] args;

        CommandInput(Object sender, String[] args) {
            this.sender = sender;
            this.args = args;
        }

        public String getPrimary() {
            return args.length == 0 ? null : args[0];
        }

        String getPrimaryData() {
            return getPrimary().toLowerCase();
        }

        public void removeArgument(int index) {
            args = ArrayUtils.remove(args, index);
        }

        public void next() {
            args = ArrayUtils.remove(args, 0);
        }

        public boolean hasNext() {
            return args.length > 0;
        }

        public Object getSender() {
            return this.sender;
        }

        public String[] getArgs() {
            return this.args;
        }
    }
}
