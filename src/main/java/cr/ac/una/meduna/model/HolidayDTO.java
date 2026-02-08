package cr.ac.una.meduna.model;

public class HolidayDTO {
    private String date;      
    private String localName; 
    private String name;      

    public HolidayDTO() {}

    public String getDate() { 
        return date; 
    }
    
    public void setDate(String date) { 
        this.date = date; 
    }

    public String getLocalName() {
        return localName; 
    }
    
    public void setLocalName(String localName) { 
        this.localName = localName; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
}
