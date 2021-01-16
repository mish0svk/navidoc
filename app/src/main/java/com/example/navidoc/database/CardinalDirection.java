package com.example.navidoc.database;

import java.io.Serializable;

@SuppressWarnings("unused")
public enum CardinalDirection implements Serializable
{
    NORTH("North"),
    NORTH_EAST("North East"),
    EAST("East"),
    SOUTH_EAST("South East"),
    SOUTH("South"),
    SOUTH_WEST("South West"),
    WEST("West"),
    NORTH_WEST("North West");

    private final String name;

    CardinalDirection(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public static CardinalDirection fromName(String name)
    {
        switch (name)
        {
            case "North":
                return CardinalDirection.NORTH;
            case "North East":
                return CardinalDirection.NORTH_EAST;
            case "East":
                return CardinalDirection.EAST;
            case "South East":
                return CardinalDirection.SOUTH_EAST;
            case "South":
                return CardinalDirection.SOUTH;
            case "South West":
                return CardinalDirection.SOUTH_WEST;
            case "West":
                return CardinalDirection.WEST;
            case "North West":
                return CardinalDirection.NORTH_WEST;
            default:
                throw new IllegalArgumentException("name '" + name + "' is not supported.");
        }
    }
}
