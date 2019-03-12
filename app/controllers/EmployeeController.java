package controllers;

import models.Employee;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.TypedQuery;


public class EmployeeController extends Controller
{
    private JPAApi db;

    @Inject
    public EmployeeController(JPAApi db)
    {
        this.db = db;
    }

    @Transactional(readOnly = true)
    public Result getEmployee()
    {
        TypedQuery<Employee> query = db.em().createQuery("SELECT e FROM Employee e WHERE employeeId = 1", Employee.class);
        Employee employee = query.getSingleResult();

        return ok(employee.getFirstName());
    }
}
