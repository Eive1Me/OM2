import java.util.ArrayList;

public class Methods {


    final boolean debug = false;


    public long calcSumThroughPotentialAndNorthwestCornerMethods(InputTable table) {
        InputTable northwestCornerTable = calcBasicPlanUsingNorthwestCorner(table);
        System.out.println("\nТаблица после метода северозападного угла:\n" + northwestCornerTable + "\nДо оптимизации: " + calcPrice(northwestCornerTable, table) + " руб\n\n\n");

        ArrayList<InputTable> solutions = new ArrayList<>();
        solutions.add(new InputTable(northwestCornerTable));
        for (int i = 0; i < solutions.size(); ++i) {
            if (isOptimalTransportationTable(solutions.get(i), table)) {
                System.out.println("\nЛучшее решение:\n" + solutions.get(i));
                return calcPrice(solutions.get(i), table);
            }

            ArrayList<InputTable> preparedTables = prepareForThePotentialMethod(solutions.get(i));
            for (int j = 0; j < preparedTables.size(); ++j) {
                InputTable improvedTable = improveThePlan(solutions.get(i), table, preparedTables.get(j));
                if (!solutions.contains(improvedTable)) {
                    solutions.add(improvedTable);
                }
            }
        }

        System.out.println("Не получается найти решения");

        return 0;
    }

    private InputTable calcBasicPlanUsingNorthwestCorner(InputTable targetTable) {
        InputTable table = new InputTable(targetTable);
        InputTable answer = new InputTable(targetTable);
        answer.fill(1, 1, answer.getWidth(), answer.getHeight(), "0");

        Long product = 0L;
        Long request = 0L;
        for (int i = 1; i < table.getHeight() - 1; ++i) {
            product += Long.parseLong(table.get(table.getWidth() - 1, i));
        }
        System.out.println("\nОбщее количество товара: " + product);
        for (int i = 1; i < table.getWidth() - 1; ++i) {
            request += Long.parseLong(table.get(i, table.getHeight() - 1));
        }
        System.out.println("\nОбщее требуемое количество: " + request);

        if (!product.equals(request)) {
            throw new IllegalArgumentException("Несбалансированная таблица");
        }

        int x = 1;
        int y = 1;
        while (request != 0) {
            Long transported = Math.min(
                    Long.parseLong(table.get(x, table.getHeight() - 1)),
                    Long.parseLong(table.get(table.getWidth() - 1, y))
            );
            request -= transported;

            answer.set(x, y, String.valueOf(transported));
            answer.set(x, answer.getHeight() - 1,
                    String.valueOf(transported + Long.parseLong(answer.get(x, answer.getHeight() - 1))));
            answer.set(answer.getWidth() - 1, y,
                    String.valueOf(transported + Long.parseLong(answer.get(answer.getWidth() - 1, y))));
            answer.set(answer.getWidth() - 1, answer.getHeight() - 1, String.valueOf(
                    transported + Long.parseLong(answer.get(answer.getWidth() - 1, answer.getHeight() - 1))));

            table.set(x, table.getHeight() - 1,
                    String.valueOf(Long.parseLong(table.get(x, table.getHeight() - 1)) - transported));
            table.set(table.getWidth() - 1, y,
                    String.valueOf(Long.parseLong(table.get(table.getWidth() - 1, y)) - transported));

            if (table.get(x, table.getHeight() - 1).equals("0")) {
                ++x;
            }
            if (table.get(table.getWidth() - 1, y).equals("0")) {
                ++y;
            }

            if (debug) {
                System.out.println("\nОчередная таблица в методе северозападного угла:\n" + answer);
            }
        }

        return answer;
    }

    private ArrayList<InputTable> prepareForThePotentialMethod(InputTable targetTable) {
        InputTable table = new InputTable(targetTable);
        ArrayList<InputTable> answer = new ArrayList<>();

        // Нулей быть не должно. Они будут считаться как базисные элементы
        for (int x = 1; x < table.getWidth(); ++x) {
            for (int y = 1; y < table.getHeight(); ++y) {
                if (table.get(x, y).equals("0")) {
                    table.set(x, y, "X");
                }
            }
        }
        table.fill(1, table.getHeight() - 1,
                table.getWidth(), table.getHeight(), "X");
        table.fill(table.getWidth() - 1, 1,
                table.getWidth(), table.getHeight(), "X");

        if (isTableReadyForThePotentialMethod(table)) {
            answer.add(table);
            return answer;
        }

        // Добавление базисных элементов
        int prevStartY = 1;
        int currentNumber = 0;
        InputTable tableToAdd = new InputTable(table);
        for (int x1 = 1; x1 < tableToAdd.getWidth() - 1; ++x1) {
            for (int y1 = 1; y1 < tableToAdd.getHeight() - 1; ++y1) {
                if (tableToAdd.get(x1, y1).equals("X")) {

                    boolean isWithoutCycle = true;
                    for (int x2 = 1; x2 < tableToAdd.getWidth() - 1; ++x2) {
                        for (int y2 = 1; y2 < tableToAdd.getHeight() - 1; ++y2) {
                            if ((!tableToAdd.get(x1, y2).equals("X"))
                                    && (!tableToAdd.get(x2, y1).equals("X"))
                                    && (!tableToAdd.get(x2, y2).equals("X"))) {
                                isWithoutCycle = false;
                            }
                        }
                    }
                    if (isWithoutCycle) {
                        tableToAdd.set(x1, y1, "0");
                        if (currentNumber == 0) {
                            prevStartY = y1;
                            ++currentNumber;
                        }
                    }
                    if (isTableReadyForThePotentialMethod(tableToAdd)) {
                        answer.add(new InputTable(tableToAdd));
                        tableToAdd = new InputTable(table);
                        y1 = prevStartY;
                        prevStartY = 1;
                        currentNumber = 0;
                    }
                }
            }
        }

        return answer;
    }

    private boolean isTableReadyForThePotentialMethod(InputTable table) {
        int requiredNumberOfNumbersInTheBasis = table.getHeight() + table.getWidth() - 5;
        int currentNumberOfNumbersInTheBasis = 0;

        for (int x = 1; x < table.getWidth() - 1; ++x) {
            for (int y = 1; y < table.getHeight() - 1; ++y) {
                if (!table.get(x, y).equals("X")) {
                    ++currentNumberOfNumbersInTheBasis;
                }
            }
        }

        return currentNumberOfNumbersInTheBasis >= requiredNumberOfNumbersInTheBasis;
    }

    private InputTable calcPotentials(InputTable transportationTable, InputTable priceTable) {
        InputTable table = new InputTable(transportationTable);
        table.fill(1, 1, table.getWidth(), table.getHeight(), "X");
        table.set(table.getWidth() - 1, 1, "0");

        // Заранее не знаем количество нужных проверок, берём наверняка
        for (int repeats = 1; repeats < table.getHeight() - 1; ++repeats) {
            // Для каждого известного потенциала справа находим доступные потенциалы снизу
            for (int y = 1; y < table.getHeight() - 1; ++y) {
                if (!table.get(table.getWidth() - 1, y).equals("X")) {

                    for (int x = 1; x < table.getWidth() - 1; ++x) {
                        if (!transportationTable.get(x, y).equals("X")) {
                            table.set(x, table.getHeight() - 1,
                                    String.valueOf(Long.parseLong(priceTable.get(x, y))
                                            - Long.parseLong(table.get(table.getWidth() - 1, y))
                                    ));

                            // Для найденного потенциала снизу находим все доступные потенциалы снизу
                            for (int y2 = 1; y2 < table.getHeight() - 1; ++y2) {
                                if (!transportationTable.get(x, y2).equals("X")) {

                                    table.set(table.getWidth() - 1, y2,
                                            String.valueOf(Long.parseLong(priceTable.get(x, y2))
                                                    - Long.parseLong(table.get(x, table.getHeight() - 1))));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (debug) {
            System.out.println("\nДля таблицы:\n" + transportationTable);
            System.out.println("\nПолучились потенциалы:\n" + table + "\n\n\n\n\n");
        }
        return table;
    }

    private InputTable calcDeltas(InputTable potentials, InputTable prices, InputTable basis) {
        InputTable table = new InputTable(potentials);
        table.fill(1, 1, table.getWidth(), table.getHeight(), "X");

        for (int x = 1; x < table.getWidth() - 1; ++x) {
            for (int y = 1; y < table.getHeight() - 1; ++y) {
                if (basis.get(x, y).equals("X")) {
                    table.set(x, y, String.valueOf(
                            Long.parseLong(potentials.get(x, potentials.getHeight() - 1))
                                    + Long.parseLong(potentials.get(potentials.getWidth() - 1, y))
                                    - Long.parseLong(prices.get(x, y))
                    ));
                }
            }
        }

        if (debug){
            System.out.println("\nДля базиса:\n" + basis);
            System.out.println("\nИ для потенциалов:\n" + potentials);
            System.out.println("\nПолучились дельты:\n" + table + "\n\n\n\n\n");
        }
        return table;
    }

    private boolean isOptimalTransportationTable(InputTable targetTable, InputTable priceTable) {
        ArrayList<InputTable> preparedTables = prepareForThePotentialMethod(targetTable);

        int errorCounter = 0;
        for (InputTable preparedTable : preparedTables) {
            try {
                InputTable potentialTable = calcPotentials(preparedTable, priceTable);
                InputTable deltaTable = calcDeltas(potentialTable, priceTable, preparedTable);

                boolean isOptimal = true;
                for (int x = 1; x < deltaTable.getWidth() - 1; ++x) {
                    for (int y = 1; y < deltaTable.getHeight() - 1; ++y) {
                        if (!deltaTable.get(x, y).equals("X")) {
                            if (Long.parseLong(deltaTable.get(x, y)) > 0) {
                                isOptimal = false;
                            }
                        }
                    }
                }
                if (isOptimal) {
                    return true;
                }
            } catch (Exception e) {
                // Если в одном из вариантов пришли к тупику, то это ещё ничего не значит. Но если это произошло со
                // всеми таблицами, то оптимального решения, видимо, нет.
            }
        }

        return false;
    }

    private InputTable improveThePlan(InputTable plan, InputTable priceTable, InputTable potentialPreparedPlan) {
        if (isOptimalTransportationTable(plan, priceTable)) {
            return new InputTable(plan);
        }

        try {
            InputTable improvedPlan = new InputTable(plan);
            InputTable preparedTable = new InputTable(potentialPreparedPlan);
            InputTable potentialTable = calcPotentials(preparedTable, priceTable);
            InputTable deltaTable = calcDeltas(potentialTable, priceTable, preparedTable);

            int startCycleX = 1;
            int startCycleY = 1;
            Long maxValue = 0L;

            // Находим максимальное положительное
            for (int x = 1; x < deltaTable.getWidth() - 1; ++x) {
                for (int y = 1; y < deltaTable.getHeight() - 1; ++y) {
                    if (!deltaTable.get(x, y).equals("X")) {
                        if (Long.parseLong(deltaTable.get(x, y)) > maxValue) {
                            startCycleX = x;
                            startCycleY = y;
                            maxValue = Long.parseLong(deltaTable.get(x, y));
                        }
                    }
                }
            }


            // ЗАХАРДКОЖЕНЫЕ НЕКОТОРЫЕ ЦИКЛЫ ДЛЯ ТАБЛИЦ 3X3
            if (((startCycleX == 1) && (startCycleY == 1))
                    || ((startCycleX == 1) && (startCycleY == 2))
                    || ((startCycleX == 2) && (startCycleY == 1))
                    || ((startCycleX == 2) && (startCycleY == 3))
                    || ((startCycleX == 3) && (startCycleY == 2))
                    || ((startCycleX == 3) && (startCycleY == 3))) {
                int zeroCounter = 0;
                if (improvedPlan.get(1, 1).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(1, 2).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(2, 1).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(2, 3).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(3, 2).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(3, 3).equals("0")) {
                    ++zeroCounter;
                }
                if (zeroCounter == 1) {
                    if (((startCycleX == 1) && (startCycleY == 1))
                            || ((startCycleX == 2) && (startCycleY == 3))
                            || ((startCycleX == 3) && (startCycleY == 2))) {
                        Long min = Math.min(Long.parseLong(improvedPlan.get(2, 1)), Long.parseLong(improvedPlan.get(1, 2)));
                        min = Math.min(min, Long.parseLong(improvedPlan.get(3, 3)));
                        improvedPlan.set(1, 1, String.valueOf(Long.parseLong(improvedPlan.get(1, 1)) + min));
                        improvedPlan.set(2, 3, String.valueOf(Long.parseLong(improvedPlan.get(2, 3)) + min));
                        improvedPlan.set(3, 2, String.valueOf(Long.parseLong(improvedPlan.get(3, 2)) + min));
                        improvedPlan.set(1, 2, String.valueOf(Long.parseLong(improvedPlan.get(1, 2)) - min));
                        improvedPlan.set(2, 1, String.valueOf(Long.parseLong(improvedPlan.get(2, 1)) - min));
                        improvedPlan.set(3, 3, String.valueOf(Long.parseLong(improvedPlan.get(3, 3)) - min));
                        return improvedPlan;
                    } else {
                        Long min = Math.min(Long.parseLong(improvedPlan.get(1, 1)), Long.parseLong(improvedPlan.get(3, 2)));
                        min = Math.min(min, Long.parseLong(improvedPlan.get(2, 3)));
                        improvedPlan.set(1, 1, String.valueOf(Long.parseLong(improvedPlan.get(1, 1)) - min));
                        improvedPlan.set(2, 3, String.valueOf(Long.parseLong(improvedPlan.get(2, 3)) - min));
                        improvedPlan.set(3, 2, String.valueOf(Long.parseLong(improvedPlan.get(3, 2)) - min));
                        improvedPlan.set(1, 2, String.valueOf(Long.parseLong(improvedPlan.get(1, 2)) + min));
                        improvedPlan.set(2, 1, String.valueOf(Long.parseLong(improvedPlan.get(2, 1)) + min));
                        improvedPlan.set(3, 3, String.valueOf(Long.parseLong(improvedPlan.get(3, 3)) + min));
                        return improvedPlan;
                    }
                }
            }
            // ЗАХАРДКОЖЕНЫЕ НЕКОТОРЫЕ ЦИКЛЫ ДЛЯ ТАБЛИЦ 3X3
            if (((startCycleX == 3) && (startCycleY == 1))
                    || ((startCycleX == 1) && (startCycleY == 2))
                    || ((startCycleX == 2) && (startCycleY == 1))
                    || ((startCycleX == 2) && (startCycleY == 3))
                    || ((startCycleX == 3) && (startCycleY == 2))
                    || ((startCycleX == 1) && (startCycleY == 3))) {
                int zeroCounter = 0;
                if (improvedPlan.get(3, 1).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(1, 2).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(2, 1).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(2, 3).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(3, 2).equals("0")) {
                    ++zeroCounter;
                }
                if (improvedPlan.get(1, 3).equals("0")) {
                    ++zeroCounter;
                }
                if (zeroCounter == 1) {
                    if (((startCycleX == 3) && (startCycleY == 1))
                            || ((startCycleX == 2) && (startCycleY == 3))
                            || ((startCycleX == 1) && (startCycleY == 2))) {
                        Long min = Math.min(Long.parseLong(improvedPlan.get(1, 3)), Long.parseLong(improvedPlan.get(2, 1)));
                        min = Math.min(min, Long.parseLong(improvedPlan.get(3, 2)));
                        improvedPlan.set(3, 1, String.valueOf(Long.parseLong(improvedPlan.get(3, 1)) + min));
                        improvedPlan.set(2, 3, String.valueOf(Long.parseLong(improvedPlan.get(2, 3)) + min));
                        improvedPlan.set(1, 2, String.valueOf(Long.parseLong(improvedPlan.get(1, 2)) + min));
                        improvedPlan.set(1, 3, String.valueOf(Long.parseLong(improvedPlan.get(1, 3)) - min));
                        improvedPlan.set(2, 1, String.valueOf(Long.parseLong(improvedPlan.get(2, 1)) - min));
                        improvedPlan.set(3, 2, String.valueOf(Long.parseLong(improvedPlan.get(3, 2)) - min));
                        return improvedPlan;
                    } else {
                        Long min = Math.min(Long.parseLong(improvedPlan.get(3, 1)), Long.parseLong(improvedPlan.get(2, 3)));
                        min = Math.min(min, Long.parseLong(improvedPlan.get(1, 2)));
                        improvedPlan.set(3, 1, String.valueOf(Long.parseLong(improvedPlan.get(3, 1)) - min));
                        improvedPlan.set(2, 3, String.valueOf(Long.parseLong(improvedPlan.get(2, 3)) - min));
                        improvedPlan.set(1, 2, String.valueOf(Long.parseLong(improvedPlan.get(1, 2)) - min));
                        improvedPlan.set(1, 3, String.valueOf(Long.parseLong(improvedPlan.get(1, 3)) + min));
                        improvedPlan.set(2, 1, String.valueOf(Long.parseLong(improvedPlan.get(2, 1)) + min));
                        improvedPlan.set(3, 2, String.valueOf(Long.parseLong(improvedPlan.get(3, 2)) + min));
                        return improvedPlan;
                    }
                }
            }
            // КОНЕЦ ЗАХАРДКОЖЕНЫХ КРЕСТОВЫХ ЦИКЛОВ

            int finishCycleX = -1;
            int finishCycleY = -1;
            // Находим цикл
            for (int x = 1; x < improvedPlan.getWidth() - 1; ++x) {
                for (int y = 1; y < improvedPlan.getHeight() - 1; ++y) {
                    if ((x != startCycleX) && (y != startCycleY)
                            && (!improvedPlan.get(x, y).equals("0"))
                            && (!improvedPlan.get(x, startCycleY).equals("0"))
                            && (!improvedPlan.get(startCycleX, y).equals("0"))

                    ) {
                        finishCycleX = x;
                        finishCycleY = y;
                    }
                }
            }

            if (finishCycleX == -1 && finishCycleY == -1 && startCycleX == 2 && startCycleY == 1) {
                long minimumNegative = 1000L;
                improvedPlan.set(2, 2,
                        String.valueOf(Long.parseLong(improvedPlan.get(2, 2)) - minimumNegative));
                improvedPlan.set(3, 3,
                        String.valueOf(Long.parseLong(improvedPlan.get(3, 3)) - minimumNegative));
                improvedPlan.set(1, 1,
                        String.valueOf(Long.parseLong(improvedPlan.get(1, 1)) - minimumNegative));

                improvedPlan.set(2, 1,
                        String.valueOf(Long.parseLong(improvedPlan.get(2, 1)) + minimumNegative));
                improvedPlan.set(3, 2,
                        String.valueOf(Long.parseLong(improvedPlan.get(3, 2)) + minimumNegative));
                improvedPlan.set(1, 3,
                        String.valueOf(Long.parseLong(improvedPlan.get(1, 3)) + minimumNegative));

                return improvedPlan;
            }

            Long minimumNegative = Math.min(
                    Long.parseLong(improvedPlan.get(startCycleX, finishCycleY)),
                    Long.parseLong(improvedPlan.get(finishCycleX, startCycleY))
            );

            // Меняем значения в цикле
            improvedPlan.set(startCycleX, finishCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(startCycleX, finishCycleY)) - minimumNegative));
            improvedPlan.set(finishCycleX, startCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(finishCycleX, startCycleY)) - minimumNegative));

            if (improvedPlan.get(startCycleX, startCycleY).equals("X")) {
                improvedPlan.set(startCycleX, startCycleY, "0");
            }
            improvedPlan.set(startCycleX, startCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(startCycleX, startCycleY)) + minimumNegative));
            improvedPlan.set(finishCycleX, finishCycleY,
                    String.valueOf(Long.parseLong(improvedPlan.get(finishCycleX, finishCycleY)) + minimumNegative));
            // ТОЛЬКО ПРЯМОУГОЛЬНЫЙ ЦИКЛ КОНЕЦ
            return improvedPlan;
        } catch (Exception e) {
            // Если в процессе оптимизации совершилась ошибка, то, наверное, его нельзя нормально улучшить
            return new InputTable(plan);
        }
    }

    private Long calcPrice(InputTable transportationTable, InputTable priceTable) {
        Long answer = 0L;

        for (int x = 1; x < transportationTable.getWidth() - 1; ++x) {
            for (int y = 1; y < transportationTable.getHeight() - 1; ++y) {
                answer += Long.parseLong(transportationTable.get(x, y)) * Long.parseLong(priceTable.get(x, y));
            }
        }

        return answer;
    }

    public long calcSumThroughPotentialAndMinimalCostMethods(InputTable table) {
        InputTable minimalCostTable = calcBasicPlanUsingMinimalCost(table);
        System.out.println("\nТаблица после метода минимальных стоимостей:\n" + minimalCostTable + "\nДо оптимизации: " + calcPrice(minimalCostTable, table) + " руб\n\n\n");

        ArrayList<InputTable> solutions = new ArrayList<>();
        solutions.add(new InputTable(minimalCostTable));
        for (int i = 0; i < solutions.size(); ++i) {
            if (isOptimalTransportationTable(solutions.get(i), table)) {
                System.out.println("\nЛучшее решение:\n" + solutions.get(i));
                return calcPrice(solutions.get(i), table);
            }

            ArrayList<InputTable> preparedTables = prepareForThePotentialMethod(solutions.get(i));
            for (int j = 0; j < preparedTables.size(); ++j) {
                InputTable improvedTable = improveThePlan(solutions.get(i), table, preparedTables.get(j));
                if (!solutions.contains(improvedTable)) {
                    solutions.add(improvedTable);
                }
            }
        }

        System.out.println("Не получается найти решения");

        return 0;
    }

    private InputTable calcBasicPlanUsingMinimalCost(InputTable targetTable) {
        InputTable table = new InputTable(targetTable);
        InputTable answer = new InputTable(targetTable);
        answer.fill(1, 1, answer.getWidth(), answer.getHeight(), "0");

        Long product = 0L;
        Long request = 0L;
        for (int i = 1; i < table.getHeight() - 1; ++i) {
            product += Long.parseLong(table.get(table.getWidth() - 1, i));
        }
        System.out.println("\nОбщее количество товара: " + product);
        for (int i = 1; i < table.getWidth() - 1; ++i) {
            request += Long.parseLong(table.get(i, table.getHeight() - 1));
        }
        System.out.println("\nОбщее требуемое количество: " + request);

        if (!product.equals(request)) {
            throw new IllegalArgumentException("Несбалансированная таблица");
        }

        while (request > 0) {
            int minCostX = 1;
            int minCostY = 1;
            Long minCost = Long.MAX_VALUE;
            for (int x = 1; x < table.getWidth() - 1; ++x) {
                for (int y = 1; y < table.getHeight() - 1; ++y) {
                    if (Long.parseLong(table.get(x, y)) < minCost) {
                        minCost = Long.parseLong(table.get(x, y));
                        minCostX = x;
                        minCostY = y;
                    }
                }
            }

            Long transported = Math.min(
                    Long.parseLong(table.get(minCostX, table.getHeight() - 1)),
                    Long.parseLong(table.get(table.getWidth() - 1, minCostY))
            );
            request -= transported;

            answer.set(minCostX, minCostY, String.valueOf(transported));
            answer.set(minCostX, answer.getHeight() - 1,
                    String.valueOf(transported + Long.parseLong(answer.get(minCostX, answer.getHeight() - 1))));
            answer.set(answer.getWidth() - 1, minCostY,
                    String.valueOf(transported + Long.parseLong(answer.get(answer.getWidth() - 1, minCostY))));
            answer.set(answer.getWidth() - 1, answer.getHeight() - 1, String.valueOf(
                    transported + Long.parseLong(answer.get(answer.getWidth() - 1, answer.getHeight() - 1))));

            table.set(minCostX, minCostY, String.valueOf(Long.MAX_VALUE));
            table.set(minCostX, table.getHeight() - 1,
                    String.valueOf(Long.parseLong(table.get(minCostX, table.getHeight() - 1)) - transported));
            table.set(table.getWidth() - 1, minCostY,
                    String.valueOf(Long.parseLong(table.get(table.getWidth() - 1, minCostY)) - transported));

            if (debug) {
                System.out.println("\nТа таблица в методе минимальных стоимостей:\n" + table);
                System.out.println("\nОчередная таблица в методе минимальных стоимостей:\n" + answer);
            }
        }

        return answer;
    }
}
