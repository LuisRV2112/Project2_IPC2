package dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import models.Appointment;

public class AvailabilityDAO {
    public static List<String> getAvailableSlots(int employeeId, int serviceId, LocalDate date) {
        List<String> slots = new ArrayList<>();
        
        // 1. Obtener duración del servicio
        int duration = ServiceDAO.getServiceById(serviceId).getDurationMin();
        
        // 2. Obtener horario del salón
        LocalDateTime opening = SalonScheduleDAO.getOpeningTime(date);
        LocalDateTime closing = SalonScheduleDAO.getClosingTime(date);
        
        // 3. Obtener citas existentes
        List<Appointment> appointments = AppointmentDAO.getEmployeeAppointments(employeeId, opening, closing);
        
        // 4. Generar slots disponibles
        LocalTime current = opening.toLocalTime();
        while (current.plusMinutes(duration).isBefore(closing.toLocalTime())) {
            boolean available = true;
            
            // Verificar colisión con citas existentes
            for (Appointment appt : appointments) {
                if (current.isAfter(appt.getStartTime().toLocalTime()) 
                    && current.isBefore(appt.getEndTime().toLocalTime())) {
                    available = false;
                    break;
                }
            }
            
            if (available) {
                slots.add(current.toString() + " - " + current.plusMinutes(duration).toString());
            }
            
            current = current.plusMinutes(15); // Intervalos de 15 minutos
        }
        
        return slots;
    }
}