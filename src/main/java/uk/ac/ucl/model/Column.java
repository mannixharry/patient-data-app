package main.java.comp004cw.model;

import java.util.ArrayList;

public class Column {
    private String name; 
    private ArrayList<String> rows; 
    
    public Column(String name)
    {
        this.name = name;
        this.rows = new ArrayList<>();
    }


    public String getName()
    {
        return name; 
    }

    public int getSize()
    {
        return rows.size();
    }

    public String getRowValue(int index)
    {
        return rows.get(index);
    }

    public void setRowValue(int index, String value) throws IndexOutOfBoundsException
    {
        if (index < 0 || index >= rows.size())
        {
            throw new IndexOutOfBoundsException("Invalid row index: " + index);
        }

        rows.set(index, value);
    }

    public void addRowValue(String value)
    {
        rows.add(value);
    }

}
