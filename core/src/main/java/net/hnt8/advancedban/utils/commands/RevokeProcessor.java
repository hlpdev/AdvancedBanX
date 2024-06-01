package net.hnt8.advancedban.utils.commands;

import net.hnt8.advancedban.Universal;
import net.hnt8.advancedban.manager.MessageManager;
import net.hnt8.advancedban.utils.Command;
import net.hnt8.advancedban.utils.Punishment;
import net.hnt8.advancedban.utils.PunishmentType;
import net.hnt8.advancedban.utils.CommandUtils;

import java.util.function.Consumer;

public class RevokeProcessor implements Consumer<Command.CommandInput> {
    private PunishmentType type;

    public RevokeProcessor(PunishmentType type) {
        this.type = type;
    }

    @Override
    public void accept(Command.CommandInput input) {
        String name = input.getPrimary();

        String target = name;
        if(!target.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            target = CommandUtils.processName(input);
            if (target == null)
                return;
        }

        Punishment punishment = CommandUtils.getPunishment(target, type);
        if (punishment == null) {
            MessageManager.sendMessage(input.getSender(), "Un" + type.getName() + ".NotPunished",
                    true, "NAME", name);
            return;
        }

        final String operator = Universal.get().getMethods().getName(input.getSender());
        punishment.delete(operator, false, true);
        MessageManager.sendMessage(input.getSender(), "Un" + type.getName() + ".Done",
                true, "NAME", name);
    }
}
