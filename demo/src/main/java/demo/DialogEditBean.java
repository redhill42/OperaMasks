/*
 * $Id:
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package demo;

import java.util.ArrayList;
import java.util.List;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.layout.impl.UIWindow;
import org.operamasks.faces.component.widget.grid.UIDataGrid;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class DialogEditBean {
    private UIDataGrid grid;
    private int row = 0;
    private List<Employee> employees;
    private Employee employee;
    
    public DialogEditBean() {
        employees = new ArrayList<Employee>();
        initEmployees();
        employee = new Employee();
    }

    public UIDataGrid getGrid() {
        return grid;
    }

    public void setGrid(UIDataGrid grid) {
        this.grid = grid;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
    public List<Employee> getEmployees() {
        return employees;
    }
    
    public Employee getEmployee() {
        grid.setRowIndex(row);
        if(grid.isRowAvailable()) {
            this.employee = (Employee) grid.getRowData();
        }
        return this.employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    private void initEmployees() {
        Employee item = new Employee();
        item.setName("看了又看");
        item.setAddr("北京市");
        item.setEmail("random@operamasks.org");
        item.setTel("010-11223344");
        employees.add(item);

        item = new Employee();
        item.setName("高手高手高高手");
        item.setAddr("广州市");
        item.setEmail("random@163.com");
        item.setTel("020-22335629");
        employees.add(item);

        item = new Employee();
        item.setName("薛定谔的猫");
        item.setAddr("深圳市");
        item.setEmail("random@yahoo.com");
        item.setTel("0755-123418181");
        employees.add(item);

        item = new Employee();
        item.setName("天才阿义");
        item.setAddr("上海市");
        item.setEmail("random@sohu.com");
        item.setTel("021-88992211");
        employees.add(item);

        item = new Employee();
        item.setName("叶闽平");
        item.setAddr("成都市");
        item.setEmail("random@sina.com.cn");
        item.setTel("121212123");
        employees.add(item);

        item = new Employee();
        item.setName("赖头");
        item.setAddr("重庆市");
        item.setEmail("random@21cn.com");
        item.setTel("11223344");
        employees.add(item);
        
        item = new Employee();
        item.setName("liuziy");
        item.setAddr("北京市");
        item.setEmail("random@gmail.com");
        item.setTel("010-77221234");
        employees.add(item);
    }
    
    public void nextRow() {
        selectRow(row + 1);
    }

    public void previousRow() {
        selectRow(row - 1);
    }

    private void selectRow(int row) {
        if (row < 0) {
            row = 0;
        } else if (row >= employees.size()) {
            row = employees.size() - 1;
        }
        this.row = row;

        int rows = grid.getRows();
        if (rows > 0) {
            int first = (row / rows) * rows;
            if (first != grid.getFirst()) {
                grid.setFirst(first);
                grid.reload();
            }
        }
    }
    
    public static final class Employee {
        String name;
        String tel;
        String addr;
        String email;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getTel() {
            return tel;
        }
        public void setTel(String tel) {
            this.tel = tel;
        }
        public String getAddr() {
            return addr;
        }
        public void setAddr(String addr) {
            this.addr = addr;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }
    
    private UIWindow editDialog;

    public UIWindow getEditDialog() {
        return editDialog;
    }

    public void setEditDialog(UIWindow editDialog) {
        this.editDialog = editDialog;
    }
    
    public void showDialog() {
        editDialog.show();
    }
    
    public void closeDialog() {
        editDialog.close();
    }
    
    public void save() {
        grid.setRowIndex(row);
        if(grid.isRowAvailable()&&this.employee != null) {
            Employee original = (Employee) grid.getRowData();
            original.setName(employee.getName());
            original.setAddr(employee.getAddr());
            original.setTel(employee.getTel());
            original.setEmail(employee.getEmail());
            grid.reload();
        }
    }

}
