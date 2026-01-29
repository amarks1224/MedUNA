/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.meduna.model;

/**
 *
 * @author juans
 */
public class EstadisticaDTO {
    private String categoria;  
    private Long cantidad;

    public EstadisticaDTO(String categoria, Long cantidad) {
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public String getCategoria() { return categoria; }
    public Long getCantidad() { return cantidad; }

    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setCantidad(Long cantidad) { this.cantidad = cantidad; }
}
