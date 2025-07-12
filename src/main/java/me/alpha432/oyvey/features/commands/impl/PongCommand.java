package me.alpha432.oyvey.features.commands.impl;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.commands.Command;
import net.minecraft.util.Formatting;

public class PongCommand
        extends Command {
    public PongCommand() {
        super("ping");
    }
    @Override
    public void execute(String[] commands) {
        PongCommand.sendMessage("Pong!");
            }

        }
