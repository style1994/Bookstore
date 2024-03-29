package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel
{
    private Vector<Vector<Object>> data;
    private Vector<String> column;
    private Vector<String> columnName;
    private Vector<String> columnType;

    public MyTableModel(Vector<Vector<Object>> data, Vector<String> column)
    {
        this.data = data;
        this.column = column;

    }

    // 顯示列數
    @Override
    public int getRowCount()
    {
        return data.size();
    }

    // 顯示欄數
    @Override
    public int getColumnCount()
    {

        return column.size();
    }

    // 取得Row列Column值
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {

        Vector<Object> obj = data.get(rowIndex);

        return obj.get(columnIndex);
    }

    // 重寫此方法，讓JTable能判斷型態
    @Override
    public Class getColumnClass(int c)
    {

        return columnType.get(c).getClass();

    }

    // 設定JTable可否被編輯
    @Override
    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    // 讓JTable能抓到欄位名稱
    @Override
    public String getColumnName(int col)
    {
        if (columnName.size() > 0)
        {
            return columnName.get(col);
        } else
        {
            return column.get(col);
        }

    }

    // JTable新增資料方法
    public void addTableData(HashMap<String, String> map)
    {
        Vector<Object> obj = new Vector<>(); // 要放入的新資料

        int column = 0; // 控制column找到相對應class
        for (Entry<String, String> entry : map.entrySet())
        {
            String value = entry.getValue();

            if (getColumnClass(column) == java.sql.Date.class) // 當欄位型態是java.sql.data 加入一個data物件
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                try
                {
                    java.util.Date date = format.parse(value);
                    Long second = date.getTime();
                    obj.add(new java.sql.Date(second));

                } catch (ParseException e)
                {
                    System.out.println("轉換日期發生錯誤");
                    e.printStackTrace();
                }

            } else if (getColumnClass(column) == java.lang.Integer.class)
            {
                int i = Integer.parseInt(value);
                obj.add(Integer.valueOf(i));
            } else
            {
                obj.add(entry.getValue()); // 都不是就當字串型態新增
            }
            column++; // index++

        }
        data.add(obj); // 將資料加入到data內
        fireTableDataChanged(); // 更新JTable
    }

    // 刪除Model資料
    public void delTableData(int row)
    {
        data.remove(row); // 刪除被選中的row
        fireTableDataChanged();

    }

    // 拿到PK的值
    public Vector<String> getPrimaryKeyValue(int row, Vector<String> primaryKey)
    {

        Vector<String> value = new Vector<>();
        for (String string : primaryKey)
        {
            int col = column.indexOf(string);
            Vector<Object> obj = data.get(row);

            value.add(obj.get(col).toString());
        }

        return value;

    }

    // 拿到那一列所有欄位值
    public Vector<String> getRowValues(int row)
    {
        Vector<String> value = new Vector<>();
        Vector<Object> temp = data.get(row);
        for (Object obj : temp)
        {
            if (obj == null)
            {
                value.add("");
            } else
            {
                value.add(obj.toString());
            }

        }

        return value;
    }

    // 更新Model資料
    public void updateTableData(int row, LinkedHashMap<String, String> dataMap)
    {

        Vector<Object> objList = new Vector<>();

        int column = 0; // 遍歷dataMap時的指標

        for (Entry<String, String> entry : dataMap.entrySet())
        {
            String type = getColumnClass(column).getName();

            if (type.equals("java.lang.String")) // 如果是字串，直接把值加入
            {
                objList.add(entry.getValue());
            } else if (type.equals("java.sql.Date")) // 如果是sql.date物件
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                try
                {
                    java.util.Date utilDate = format.parse(entry.getValue());
                    long second = utilDate.getTime();

                    objList.add(new java.sql.Date(second));
                } catch (ParseException e)
                {
                    System.out.println("透過simple date format轉換失敗");
                    e.printStackTrace();
                }

            } else if (type.equals("java.lang.Integer")) // 如果是Integer
            {
                objList.add(Integer.valueOf(entry.getValue()));
            }

            column++; // index++

        } // for each

        // 移除掉所選取Row的資料,再把修改資料重新加到原有Index值
        data.remove(row);
        data.add(row, objList);
        fireTableDataChanged(); // 更新JTable

    }

    // 設定Model資料
    public void setTableData(Vector<Vector<Object>> data)
    {
        this.data = data;

        fireTableDataChanged(); // 更新JTable
    }

    // 設定Model欄位名
    public void setColumnName(Vector<String> columnName)
    {
        this.columnName = columnName;
    }

    // 設定Model欄位資料型態
    public void setType(Vector<String> columnType)
    {
        this.columnType = columnType;
    }
}
