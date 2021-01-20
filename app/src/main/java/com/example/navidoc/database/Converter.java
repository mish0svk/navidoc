package com.example.navidoc.database;

import androidx.room.TypeConverter;

import com.example.navidoc.utils.ArrowDirections;

public class Converter
{
    @TypeConverter
    public static String fromCardinalDirection(CardinalDirection cardinalDirection)
    {
        return  cardinalDirection.getName();
    }

    @TypeConverter
    public static CardinalDirection fromStringToCardinalDirection(String name)
    {
       return CardinalDirection.fromName(name);
    }

    @TypeConverter
    public static ArrowDirections.VectorDirection fromDegreeToArrowDirection(int degree)
    {
        switch (degree)
        {
            case 0:
                return ArrowDirections.VectorDirection.FRONT;
            case 45:
                return ArrowDirections.VectorDirection.FRONT_RIGHT;
            case 90:
                return ArrowDirections.VectorDirection.RIGHT;
            case 135:
                return ArrowDirections.VectorDirection.BACK_RIGHT;
            case 180:
                return ArrowDirections.VectorDirection.BACK;
            case 225:
                return ArrowDirections.VectorDirection.BACK_LEFT;
            case 270:
                return ArrowDirections.VectorDirection.LEFT;
            case 315:
                return ArrowDirections.VectorDirection.FRONT_LEFT;
            default:
                throw new IllegalArgumentException("Wrong degree");
        }
    }

    @TypeConverter
    public static int fromDirectionToDegree(CardinalDirection direction)
    {
        switch (direction)
        {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 45;
            case EAST:
                return 90;
            case SOUTH_EAST:
                return 135;
            case SOUTH:
                return 180;
            case SOUTH_WEST:
                return 225;
            case WEST:
                return 270;
            case NORTH_WEST:
                return 315;
            default:
                throw new IllegalArgumentException("Wrong direction");
        }
    }
}
