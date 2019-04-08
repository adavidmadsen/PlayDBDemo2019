package controllers;


import com.google.common.io.Files;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Dynamic;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.math.BigDecimal;
import java.sql.DataTruncation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class EmployeeController extends Controller

{

    private JPAApi db;
    private FormFactory formFactory;


    @Inject
    public EmployeeController(JPAApi db, FormFactory formFactory)
    {
        this.db = db;
        this.formFactory = formFactory;

    }


    @Transactional(readOnly = true)
    public Result getEmployee(int employeeId)

    {

        TypedQuery<Employee> query = db.em().createQuery("SELECT e FROM Employee e WHERE employeeId = :employeeId",
                Employee.class);
        query.setParameter("employeeId", employeeId);
        Employee employee = query.getSingleResult();

        Employee reportstoEmployee = null;
        if (employee.getReportsToEmployeeId() != null)
        {
            query.setParameter("employeeId", employee.getReportsToEmployeeId());
            reportstoEmployee = query.getSingleResult();
        }

        TypedQuery<State> statesQuery = db.em().createQuery("SELECT s FROM State s WHERE stateId = :stateId", State.class);
        statesQuery.setParameter("stateId", employee.getStateId());
        State state = statesQuery.getSingleResult();


        TypedQuery<Employee> reportsQuery = db.em().createQuery("SELECT e FROM Employee e WHERE reportsToEmployeeId =" + " :employeeId", Employee.class);
        reportsQuery.setParameter("employeeId", employeeId);
        List<Employee> reports = reportsQuery.getResultList();


        return ok(views.html.employee.render(employee, reports, reportstoEmployee, state));

    }


    @Transactional(readOnly = true)
    public Result getPicture(int employeeId)

    {

        TypedQuery<Employee> query = db.em().createQuery("SELECT e FROM Employee e WHERE employeeId = :employeeId", Employee.class);
        query.setParameter("employeeId", employeeId);
        Employee employee = query.getSingleResult();


        return ok(employee.getPicture());

    }

    @Transactional(readOnly = true)
    public Result getEmployeeEdit(int employeeId)

    {

        TypedQuery<Employee> query = db.em().createQuery("SELECT e FROM Employee e WHERE employeeId = :employeeId",
                Employee.class);
        query.setParameter("employeeId", employeeId);
        Employee employee = query.getSingleResult();

        TypedQuery<Title> titleQuery = db.em().createQuery("SELECT t FROM Title t", Title.class);
        List<Title> titles = titleQuery.getResultList();

        TypedQuery<Hobby> hobbyQuery = db.em().createQuery("SELECT h FROM Hobby h", Hobby.class);
        List<Hobby> hobbies = hobbyQuery.getResultList();


        return ok(views.html.employeeedit.render(employee, titles, hobbies));

    }

    @Transactional
    public Result postEmployeeEdit(int employeeId)

    {

        TypedQuery<Employee> query = db.em().createQuery("SELECT e FROM Employee e WHERE employeeId = :employeeId",
                Employee.class);
        query.setParameter("employeeId", employeeId);
        Employee employee = query.getSingleResult();

        DynamicForm form = formFactory.form().bindFromRequest();


        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        int titleId = Integer.parseInt(form.get("titleId"));
        int titleOfCourtesyId = Integer.parseInt(form.get("titleOfCourtesyId"));
        String hobby = form.get("hobbyId");
        Integer hobbyId = null;
        if(hobby != null && hobby.length()>0)
        {
            hobbyId = Integer.parseInt(hobby);
        }
//        int hobbyId = Integer.parseInt(form.get("hobbyId"));
        LocalDate birthdate = LocalDate.parse(form.get("birthdate"));
        LocalDate hireDate = LocalDate.parse(form.get("hireDate"));
        String address = form.get("address");
        String city = form.get("city");
        String stateId = form.get("stateId");
        String zipCode = form.get("zipCode");
        String personalPhone = form.get("personalPhone");
        String extension = form.get("extension");
        String notes = form.get("notes");
        int reportsToEmployeeId = Integer.parseInt(form.get("reportsToEmployeeId"));
        BigDecimal salary = new BigDecimal(form.get("salary"));


        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setTitleId(titleId);
        employee.setTitleOfCourtesyId(titleOfCourtesyId);
        employee.setHobbyId(hobbyId);
        employee.setBirthdate(birthdate);
        employee.setHireDate(hireDate);
        employee.setAddress(address);
        employee.setCity(city);
        employee.setStateId(stateId);
        employee.setZipCode(zipCode);
        employee.setPersonalPhone(personalPhone);
        employee.setExtension(extension);
        employee.setNotes(notes);
        employee.setReportsToEmployeeId(reportsToEmployeeId);
        employee.setSalary(salary);

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("picture");
        File file = filePart.getFile();

        byte[] picture;

        try
        {
            picture = Files.toByteArray(file);
            if(picture != null && picture.length > 0)
            {
                employee.setPicture(picture);
            }
        }
        catch(Exception e)
        {
            picture = null;
        }


        db.em().persist(employee);


        return ok("saved");

    }

    public Result getEmployeeAdd()
    {
        return ok(views.html.employeeadd.render());
    }

    @Transactional
    public Result postEmployeeAdd()

    {


        Employee employee = new Employee();

        DynamicForm form = formFactory.form().bindFromRequest();
        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        int titleId = Integer.parseInt(form.get("titleId"));
        int titleOfCOurtesyId = Integer.parseInt(form.get("titleOfCourtesyId"));
        int hobbyId = Integer.parseInt(form.get("hobbyId"));
        LocalDate birthdate = LocalDate.parse(form.get("birthdate"));
        LocalDate hireDate = LocalDate.parse(form.get("hireDate"));
        String address = form.get("address");
        String city = form.get("city");
        String stateId = form.get("stateId");
        String zipCode = form.get("zipCode");
        String personalPhone = form.get("personalPhone");
        String extension = form.get("extension");
        String notes = form.get("notes");
        int reportsToEmployeeId = Integer.parseInt(form.get("reportsToEmployeeId"));
        BigDecimal salary = new BigDecimal(form.get("salary"));


        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setTitleId(titleId);
        employee.setTitleOfCourtesyId(titleOfCOurtesyId);
        employee.setHobbyId(hobbyId);
        employee.setBirthdate(birthdate);
        employee.setHireDate(hireDate);
        employee.setAddress(address);
        employee.setCity(city);
        employee.setStateId(stateId);
        employee.setZipCode(zipCode);
        employee.setPersonalPhone(personalPhone);
        employee.setExtension(extension);
        employee.setNotes(notes);
        employee.setReportsToEmployeeId(reportsToEmployeeId);
        employee.setSalary(salary);



        db.em().persist(employee);



        return ok("saved");
    }

    @Transactional(readOnly=true)
    public Result getEmployees()
    {
        TypedQuery<Employee> query = db.em().createQuery("Select e FROM Employee e ORDER BY lastName, firstName, employeeId", Employee.class);
        List<Employee> employees = query.getResultList();

        return ok(views.html.employees.render(employees));

    }

    @Transactional(readOnly=true)
    public Result getEmployeeSearch()
    {

        DynamicForm form = formFactory.form().bindFromRequest();
        String name = form.get("name");


        if(name == null)
        {
            name = "";
        }
        name = "%"+ name + "%";
        Logger.debug("name:" + name);

        TypedQuery<EmployeeDetail> query = db.em().createQuery("Select NEW EmployeeDetail(e.employeeId, e.firstName, e.lastName, t.titleName) FROM Employee e Join Title t ON e.titleId = t.titleId " +
                "Where lastName Like :name OR FirstName Like :name ORDER BY lastName, firstName, employeeId", EmployeeDetail.class);
        query.setParameter("name", name);
        List<EmployeeDetail> employees = query.getResultList();

        return ok(views.html.employeesearch.render(employees));

    }
}