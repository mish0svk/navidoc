package com.example.navidoc.database;

import androidx.room.TypeConverter;

public class Converter
{
    @TypeConverter
    public static String fromCardinalDirection(CardinalDirection cardinalDirection)
    {
        return  cardinalDirection.getName();
    }

    @TypeConverter
    public static CardinalDirection toCardinalDirection(String name)
    {
       return CardinalDirection.fromName(name);
    }
}
