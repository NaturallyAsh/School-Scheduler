package com.example.ashleighwilson.schoolscheduler.notes;

import com.example.ashleighwilson.schoolscheduler.utils.EqualityChecker2;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseNote2 implements Serializable {
    private String title;
    private String content;
    private Long creation;
    private Long lastModification;
    private Boolean archived;
    private Boolean trashed;
    private String alarm;
    private Boolean reminderFired;
    private String recurrenceRule;
    private Double latitude;
    private Double longitude;
    private String address;
    private BaseCategory2 baseCategory;
    private Boolean locked;
    private Boolean checklist;
    private List<? extends BaseAttachment2> attachmentsList = new ArrayList();
    private transient List<? extends BaseAttachment2> attachmentsListOld = new ArrayList();

    public BaseNote2() {
        this.title = "";
        this.content = "";
        this.archived = false;
        this.trashed = false;
        this.locked = false;
        this.checklist = false;
    }

    public BaseNote2(Long creation, Long lastModification, String title, String content, Integer archived, Integer trashed, String alarm, Integer reminderFired, String recurrenceRule, String latitude, String longitude, BaseCategory2 baseCategory, Integer locked, Integer checklist) {
        this.title = title;
        this.content = content;
        this.creation = creation;
        this.lastModification = lastModification;
        this.archived = archived == 1;
        this.trashed = trashed == 1;
        this.alarm = alarm;
        this.reminderFired = reminderFired == 1;
        this.recurrenceRule = recurrenceRule;
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.baseCategory = baseCategory;
        this.locked = locked == 1;
        this.checklist = checklist == 1;
    }

    public BaseNote2(Long creation, Long lastModification, String title, String content, String alarm,
                     String recurrenceRule) {
        this.creation = creation;
        this.lastModification = lastModification;
        this.title = title;
        this.content = content;
        this.alarm = alarm;
        this.recurrenceRule = recurrenceRule;
    }

    public BaseNote2(BaseNote2 baseNote) {
        this.buildFromNote(baseNote);
    }

    private void buildFromNote(BaseNote2 baseNote) {
        this.setTitle(baseNote.getTitle());
        this.setContent(baseNote.getContent());
        this.setCreation(baseNote.getCreation());
        this.setLastModification(baseNote.getLastModification());
        this.setArchived(baseNote.isArchived());
        this.setTrashed(baseNote.isTrashed());
        this.setAlarm(baseNote.getAlarm());
        this.setRecurrenceRule(baseNote.getRecurrenceRule());
        this.setReminderFired(baseNote.isReminderFired());
        this.setLatitude(baseNote.getLatitude());
        this.setLongitude(baseNote.getLongitude());
        this.setAddress(baseNote.getAddress());
        this.setCategory(baseNote.getCategory());
        this.setLocked(baseNote.isLocked());
        this.setChecklist(baseNote.isChecklist());
        ArrayList<BaseAttachment2> list = new ArrayList();
        Iterator i$ = baseNote.getAttachmentsList().iterator();

        while(i$.hasNext()) {
            BaseAttachment2 mBaseAttachment = (BaseAttachment2)i$.next();
            list.add(mBaseAttachment);
        }

        this.setAttachmentsList(list);
    }

    public void buildFromJson(String jsonNote) {
        Gson gson = new Gson();
        BaseNote2 baseNoteFromJson = (BaseNote2)gson.fromJson(jsonNote, this.getClass());
        this.buildFromNote(baseNoteFromJson);
    }

    public void set_id(Long _id) {
        this.creation = _id;
    }

    public Long get_id() {
        return this.creation;
    }

    public String getTitle() {
        return this.title == null ? "" : this.title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public String getContent() {
        return this.content == null ? "" : this.content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
    }

    public Long getCreation() {
        return this.creation;
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public void setCreation(String creation) {
        Long creationLong;
        try {
            creationLong = Long.parseLong(creation);
        } catch (NumberFormatException var4) {
            creationLong = null;
        }

        this.creation = creationLong;
    }

    public Long getLastModification() {
        return this.lastModification;
    }

    public void setLastModification(Long lastModification) {
        this.lastModification = lastModification;
    }

    public void setLastModification(String lastModification) {
        Long lastModificationLong;
        try {
            lastModificationLong = Long.parseLong(lastModification);
        } catch (NumberFormatException var4) {
            lastModificationLong = null;
        }

        this.lastModification = lastModificationLong;
    }

    public Boolean isArchived() {
        return this.archived != null && this.archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public void setArchived(int archived) {
        this.archived = archived == 1;
    }

    public Boolean isTrashed() {
        return this.trashed != null && this.trashed;
    }

    public void setTrashed(Boolean trashed) {
        this.trashed = trashed;
    }

    public void setTrashed(int trashed) {
        this.trashed = trashed == 1;
    }

    public String getAlarm() {
        return this.alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public void setAlarm(long alarm) {
        this.alarm = String.valueOf(alarm);
    }

    public Boolean isReminderFired() {
        return this.reminderFired != null && this.reminderFired;
    }

    public void setReminderFired(Boolean reminderFired) {
        this.reminderFired = reminderFired;
    }

    public void setReminderFired(int reminderFired) {
        this.reminderFired = reminderFired == 1;
    }

    public String getRecurrenceRule() {
        return this.recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLatitude(String latitude) {
        try {
            this.setLatitude(Double.parseDouble(latitude));
        } catch (NullPointerException | NumberFormatException var3) {
            this.latitude = null;
        }

    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLongitude(String longitude) {
        try {
            this.setLongitude(Double.parseDouble(longitude));
        } catch (NumberFormatException var3) {
            this.longitude = null;
        } catch (NullPointerException var4) {
            this.longitude = null;
        }

    }

    public BaseCategory2 getCategory() {
        return this.baseCategory;
    }

    public void setCategory(BaseCategory2 baseCategory) {
        this.baseCategory = baseCategory;
    }

    public Boolean isLocked() {
        return this.locked != null && this.locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public void setLocked(int locked) {
        this.locked = locked == 1;
    }

    public Boolean isChecklist() {
        return this.checklist != null && this.checklist;
    }

    public void setChecklist(Boolean checklist) {
        this.checklist = checklist;
    }

    public void setChecklist(int checklist) {
        this.checklist = checklist == 1;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<? extends BaseAttachment2> getAttachmentsList() {
        return this.attachmentsList;
    }

    public void setAttachmentsList(List<? extends BaseAttachment2> attachmentsList) {
        this.attachmentsList = attachmentsList;
    }

    public void backupAttachmentsList() {
        List<BaseAttachment2> attachmentsListOld = new ArrayList();
        Iterator i$ = this.getAttachmentsList().iterator();

        while(i$.hasNext()) {
            BaseAttachment2 mBaseAttachment = (BaseAttachment2)i$.next();
            attachmentsListOld.add(mBaseAttachment);
        }

        this.attachmentsListOld = attachmentsListOld;
    }

    public List<? extends BaseAttachment2> getAttachmentsListOld() {
        return this.attachmentsListOld;
    }

    public void setAttachmentsListOld(List<? extends BaseAttachment2> attachmentsListOld) {
        this.attachmentsListOld = attachmentsListOld;
    }

    public boolean equals(Object o) {
        boolean res = false;

        BaseNote2 baseNote;
        try {
            baseNote = (BaseNote2)o;
        } catch (Exception var6) {
            return res;
        }

        Object[] a = new Object[]{this.getTitle(), this.getContent(), this.getCreation(), this.getLastModification(), this.isArchived(), this.isTrashed(), this.getAlarm(), this.getRecurrenceRule(), this.getLatitude(), this.getLongitude(), this.getAddress(), this.isLocked(), this.getCategory(), this.isChecklist()};
        Object[] b = new Object[]{baseNote.getTitle(), baseNote.getContent(), baseNote.getCreation(), baseNote.getLastModification(), baseNote.isArchived(), baseNote.isTrashed(), baseNote.getAlarm(), baseNote.getRecurrenceRule(), baseNote.getLatitude(), baseNote.getLongitude(), baseNote.getAddress(), baseNote.isLocked(), baseNote.getCategory(), baseNote.isChecklist()};
        if (EqualityChecker2.check(a, b)) {
            res = true;
        }

        return res;
    }

    public boolean isChanged(BaseNote2 baseNote) {
        return !this.equals(baseNote) || !this.getAttachmentsList().equals(baseNote.getAttachmentsList());
    }

    public boolean isEmpty() {
        BaseNote2 emptyBaseNote = new BaseNote2();
        emptyBaseNote.setCreation(this.getCreation());
        emptyBaseNote.setCategory(this.getCategory());
        return !this.isChanged(emptyBaseNote);
    }

    public String toString() {
        return this.getTitle();
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
