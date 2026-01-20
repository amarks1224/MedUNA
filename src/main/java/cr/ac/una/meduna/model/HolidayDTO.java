/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.meduna.model;


/**
 *
 * @author juans
 */
public class HolidayDTO {
    private String date;      // "2026-01-01"
    private String localName; // nombre local
    private String name;      // nombre en inglés

    public HolidayDTO() {}

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
