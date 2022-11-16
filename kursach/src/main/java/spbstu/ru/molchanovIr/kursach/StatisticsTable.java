package spbstu.ru.molchanovIr.kursach;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;

public class StatisticsTable {

    private LinkedList<Request> save;

    {
        save = new LinkedList<>();
    }

    public void add(Request request) {
        save.add(request);
    }

    private String doubleInstantToString(Double a) {
        return TableGUI.instantToString(Instant.ofEpochMilli(a.longValue()));
    }

    public void crate(Instant lastMoment) {
        XSSFWorkbook workBook = new XSSFWorkbook();

        {
            XSSFSheet workSheet = workBook.createSheet("характеристики источников");
            {
                XSSFRow workRow = workSheet.createRow(0);
                workRow.createCell(0, CellType.STRING).setCellValue("№ источника");
                workRow.createCell(1, CellType.STRING).setCellValue("количество заявок");
                workRow.createCell(2, CellType.STRING).setCellValue("Р отк");
                workRow.createCell(3, CellType.STRING).setCellValue("Т преб");
                workRow.createCell(4, CellType.STRING).setCellValue("Т БП");
                workRow.createCell(5, CellType.STRING).setCellValue("Т обсл");
                workRow.createCell(6, CellType.STRING).setCellValue("Д БП");
                workRow.createCell(7, CellType.STRING).setCellValue("Д обсл");
            }
            Long count = 0L, rej = 0L,
                    averTBP = 0L, averTobsl = 0L,
                    maxBP = 0L, minBP = Long.MAX_VALUE,
                    maxOBSL = 0L, minOBSL = Long.MAX_VALUE;
            for (int source = 0; source < GeneratorRequests.countSources; ++source) {
                for (Request request : save) {
                    if (request.source == source) {
                        count++;
                        if (request.finishWork == null) {
                            rej++;
                        } else {
                            averTobsl += request.duration.toEpochMilli();
                            maxOBSL = Long.max(maxOBSL, request.duration.toEpochMilli());
                            minOBSL = Long.min(minOBSL, request.duration.toEpochMilli());
                        }
                        averTBP += request.startWorkOnDevice.toEpochMilli() - request.startWork.toEpochMilli();
                        maxBP = Long.max(maxBP, request.startWorkOnDevice.toEpochMilli() - request.startWork.toEpochMilli());
                        minBP = Long.min(minBP, request.startWorkOnDevice.toEpochMilli() - request.startWork.toEpochMilli());
                    }
                }
                {
                    XSSFRow workRow = workSheet.createRow(source + 1);
                    workRow.createCell(0, CellType.STRING).setCellValue("И" + (source + 1));
                    workRow.createCell(1, CellType.NUMERIC).setCellValue(count);
                    workRow.createCell(2, CellType.NUMERIC).setCellValue(rej / count.doubleValue());
                    workRow.createCell(3, CellType.STRING).setCellValue(doubleInstantToString((averTBP + averTobsl) / count.doubleValue()));
                    workRow.createCell(4, CellType.STRING).setCellValue(doubleInstantToString(averTBP / count.doubleValue()));
                    workRow.createCell(5, CellType.STRING).setCellValue(doubleInstantToString(averTobsl / (count.doubleValue() - rej)));
                    workRow.createCell(6, CellType.STRING).setCellValue(doubleInstantToString(maxBP - minBP.doubleValue()));
                    workRow.createCell(7, CellType.STRING).setCellValue(doubleInstantToString(maxOBSL - minOBSL.doubleValue()));
                }
                count = 0L;
                rej = 0L;
                averTBP = 0L;
                averTobsl = 0L;
                maxBP = 0L;
                minBP = Long.MAX_VALUE;
                maxOBSL = 0L;
                minOBSL = Long.MAX_VALUE;
            }
            workSheet.autoSizeColumn(0);
            workSheet.autoSizeColumn(1);
            workSheet.autoSizeColumn(2);
            workSheet.autoSizeColumn(3);
            workSheet.autoSizeColumn(4);
            workSheet.autoSizeColumn(5);
            workSheet.autoSizeColumn(6);
            workSheet.autoSizeColumn(7);
        }

        {
            XSSFSheet workSheet = workBook.createSheet("Характеристики приборов");
            {
                XSSFRow workRow = workSheet.createRow(0);
                workRow.createCell(0, CellType.STRING).setCellValue("№ прибора");
                workRow.createCell(1, CellType.STRING).setCellValue("Коэффициент использования");
            }
            Double timeWork = 0.0;
            for (int device = 0; device < Devices.size; ++device) {
                for (Request request : save) {
                    if (request.numberDevice != null && request.numberDevice == device) {
                        timeWork += request.finishWork.toEpochMilli() - request.startWorkOnDevice.toEpochMilli();
                    }
                }
                {
                    XSSFRow workRow = workSheet.createRow(device + 1);
                    workRow.createCell(0, CellType.NUMERIC).setCellValue("П" + (device + 1));
                    workRow.createCell(1, CellType.NUMERIC).setCellValue(timeWork / lastMoment.toEpochMilli());
                }
                timeWork = 0.0;
            }
            workSheet.autoSizeColumn(0);
            workSheet.autoSizeColumn(1);
        }

        File result = new File("результат " + ((Instant.now().getEpochSecond() / 60 / 60 + 3) % 24)
                + "-" + TableGUI.instantToString(Instant.now())
                .replace(':', '-') + ".xlsx");
        try {
            result.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            workBook.write(new FileOutputStream(result));
            workBook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
