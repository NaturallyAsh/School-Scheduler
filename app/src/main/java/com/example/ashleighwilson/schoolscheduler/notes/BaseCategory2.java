package com.example.ashleighwilson.schoolscheduler.notes;

import java.util.Calendar;


public class BaseCategory2 {
    private Long id;
    private String name;
    private String description;
    private String color;
    private int count;

    public BaseCategory2() {
        this.id = Calendar.getInstance().getTimeInMillis();
    }

    public BaseCategory2(BaseCategory2 baseCategory) {
        this(baseCategory.getId(), baseCategory.getName(), baseCategory.getDescription(), baseCategory.getColor());
    }

    public BaseCategory2(Long id, String title, String description, String color) {
        this(id, title, description, color, 0);
    }

    public BaseCategory2(Long id, String title, String description, String color, int count) {
        this.id = id;
        this.name = title;
        this.description = description;
        this.color = color;
        this.count = count;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj.getClass().equals(this.getClass())) {
            try {
                BaseCategory2 c = (BaseCategory2)obj;
                result = (this.getColor() == c.getColor() || this.getColor().equals(c.getColor())) && (this.getDescription() == c.getDescription() || this.getDescription().equals(c.getDescription())) && (this.getName() == c.getName() || this.getName().equals(c.getName())) && (this.getId() == c.getId() || this.getId().equals(c.getId()));
            } catch (ClassCastException var4) {
                result = false;
            }
        }

        return result;
    }
}
