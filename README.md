# Моделирование операций
## Лаб №2
_Домашка, которую я абсолютно точно сделала сама._

## Входные данные

| Поставщик\Потребитель | Нижний Новгород | Пермь | Краснодар | Производство |
| ------ | ------ | ------ | ------ | ------ | 
| Омск        | 3637 | 3793 | 4509 | 1400 |
| Новосибирск | 3043 | 3165 | 3714 | 1700 |
| Томск       | 4386 | 4711 | 5607 | 1600 |
| Спрос       | 2000 | 1000 | 1700 |      |

## Решение

- Методом потенциалов с использованием метода северо-западного угла
  
- Методом потенциалов с использованием метода минимальных стоимостей

## Лучшее решение

| Поставщик\Потребитель | Нижний Новгород | Пермь | Краснодар | Производство |
| ------ | ------ | ------ | ------ | ------ | 
| Омск        |  400 | 1000 |    0 | 1400 |
| Новосибирск |    0 |    0 | 1700 | 1700 |
| Томск       | 1600 |    0 |    0 | 1600 |
| Спрос       | 2000 | 1000 | 1700 | 4700 |

Ответ: 18 579 200 руб.