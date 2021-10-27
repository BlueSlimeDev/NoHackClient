package dev.mruniverse.nohackclient.listeners.containers;

import dev.mruniverse.nohackclient.listeners.common.Sanction;

public class ContainerDetector {

    public boolean execute(Sanction currentSanction, String[] length, String name) {
        if (length.length >= currentSanction.getNumberLength()) {
            for (String containerCheck : currentSanction.getContainerText()) {
                if (!name.contains(containerCheck)) return false;
            }
            return true;
        }
        return false;
    }
}
