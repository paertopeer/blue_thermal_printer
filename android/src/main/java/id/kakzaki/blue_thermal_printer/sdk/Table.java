//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk;

import android.annotation.SuppressLint;
import id.kakzaki.blue_thermal_printer.sdk.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressLint({"UseSparseArrays"})
public class Table {
    private List<String> tableRows = new ArrayList();
    private String tableReg;
    private HashMap<Integer, String> unPrintColumnMap = new HashMap();
    private int[] tableColWidth;
    private boolean alignRight;

    public Table(String column, String regularExpression, int[] columnWidth) {
        this.tableRows.add(column);
        this.tableReg = regularExpression;
        if (columnWidth != null) {
            this.tableColWidth = columnWidth;
        } else {
            this.tableColWidth = new int[column.split(regularExpression).length];

            for(int i = 0; i < this.tableColWidth.length; ++i) {
                this.tableColWidth[i] = 8;
            }
        }

    }

    public void setColumnAlignRight(boolean right) {
        this.alignRight = right;
    }

    public void addRow(String row) {
        if (this.tableRows != null) {
            this.tableRows.add(row);
        }

    }

    public String getTableText() {
        StringBuffer sb = new StringBuffer();

        for(int m = 0; m < this.tableRows.size(); ++m) {
            String[] tableLine = ((String)this.tableRows.get(m)).split(this.tableReg);
            sb.append(this.printTableLine(tableLine));
            sb.append("\n");
        }

        return sb.toString();
    }

    private String printTableLine(String[] tableLine) {
        StringBuffer sb = new StringBuffer();
        boolean sub_length = false;
        String[] line = tableLine;

        for(int i = 0; i < line.length; ++i) {
            line[i] = line[i].trim();
            int index = line[i].indexOf("\n");
            if (index != -1) {
                this.unPrintColumnMap.put(i, line[i].substring(index + 1));
                line[i] = line[i].substring(0, index);
                sb.append(this.printTableLine(line));
                sb.append(this.printNewLine(line));
                return sb.toString();
            }

            int length = Utils.getStringCharacterLength(line[i]);
            int col_length = this.tableColWidth.length;
            int col_width = 8;
            if (i < col_length) {
                col_width = this.tableColWidth[i];
            }

            int j;
            if (length > col_width && i != line.length - 1) {
                j = Utils.getSubLength(line[i], col_width);
                this.unPrintColumnMap.put(i, line[i].substring(j, line[i].length()));
                line[i] = line[i].substring(0, j);
                sb = new StringBuffer();
                sb.append(this.printTableLine(line));
                sb.append(this.printNewLine(line));
                return sb.toString();
            }

            if (i == 0) {
                sb.append(line[i]);

                for(j = 0; j < col_width - length; ++j) {
                    sb.append(" ");
                }
            } else if (!this.alignRight) {
                sb.append(line[i]);

                for(j = 0; j < col_width - length; ++j) {
                    if (i != line.length - 1) {
                        sb.append(" ");
                    }
                }
            } else {
                for(j = 0; j < col_width - length; ++j) {
                    sb.append(" ");
                }

                sb.append(line[i]);
            }
        }

        return sb.toString();
    }

    private String printNewLine(String[] oldLine) {
        if (this.unPrintColumnMap.isEmpty()) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            String[] newLine = new String[oldLine.length];
            Iterator iterator = this.unPrintColumnMap.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry i = (Map.Entry)iterator.next();
                Integer key = (Integer)i.getKey();
                String value = (String)i.getValue();
                if (key < oldLine.length) {
                    newLine[key] = value;
                }
            }

            this.unPrintColumnMap.clear();

            for(int var8 = 0; var8 < newLine.length; ++var8) {
                if (newLine[var8] == null || newLine[var8] == "") {
                    newLine[var8] = " ";
                }
            }

            sb.append(this.printTableLine(newLine));
            return "\n" + sb.toString();
        }
    }
}
