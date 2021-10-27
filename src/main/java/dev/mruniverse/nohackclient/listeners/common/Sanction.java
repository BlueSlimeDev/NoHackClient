package dev.mruniverse.nohackclient.listeners.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Sanction {
    private final String name;

    private final String id;

    private int numberLength = 0;

    private List<String> containerText = new ArrayList<>();

    public Sanction(String name, String id, int numberLength, List<String> containerText) {
        this.name = name;
        this.id = id;
        this.numberLength = numberLength;
        this.containerText = containerText;
    }

    public Sanction(String name, String id, int numberLength) {
        this.name = name;
        this.id = id;
        this.numberLength = numberLength;
    }

    public Sanction(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Sanction(String name, String id,List<String> containerText) {
        this.name = name;
        this.id = id;
        this.containerText = containerText;
    }

    public int getNumberLength() {
        return numberLength;
    }

    public List<String> getContainerText() {
        return containerText;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
