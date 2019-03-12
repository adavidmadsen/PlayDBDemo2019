package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employee
{
    @Id
    private int employeeId;

    private String firstName;

    public int getEmployeeId()
    {
        return employeeId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
}
