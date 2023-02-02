import re

'''
    Programa que busca la ocurrencia de los invariantes definidos como un patrón o expresión regular
    en la variable "invariant" y reemplaza su ocurrencia en un string (res) por los grupos definidos
    en "groups", es decir la ocurrencia de transiciones fuera de los invariantes.
    En caso de sobrar transiciones, significa que hay un fallo en los invariantes del sistema, ya que
    al reemplazar por los grupos deberíamos obtener otros invariantes que fueron disparados por
    interleaving.
    Para que se tome como exitoso el análisis, la red deberá haber vuelto a su estado inicial.
'''

# Patrón de los distintos invariantes de Transición
invariant = '(.*?)(T9)(.*?)(T10)(.*?)(T11)(.*?)(T12)(.*?)|(.*?)(T1)(?=-)(.*?)((T3)(.*?)(T5)(.*?)(T7)(.*?)|(T2)(.*?)(T4)(.*?)(T6)(.*?))(T8)(.*?)'
# Grupos con los que se reemplaza el patrón
groups = '\g<1>\g<3>\g<5>\g<7>\g<9>\g<10>\g<12>\g<15>\g<17>\g<19>\g<21>\g<23>\g<25>\g<27>'
# Archivo log con las transiciones disparadas
file = open("data/log/logFired.txt", "r")
# Variable (tupla) con el texto del log [0] y el número de reemplazos [1]
res = (file.read().rstrip(), 0)

print(res)

# Sustituimos el patrón de invariantes por los grupos hasta que la cantidad de reemplazos sea 0
while True:
    res = re.subn(invariant, groups, res[0].rstrip())

    print(res)

    if res[1] == 0:
        break

res = re.subn('-' , '' , res[0].rstrip())

# Imprimimos el resultado del análisis
if res[0] == '':
    print("\nNo hay fallos en los invariantes\n")
else:
    print("Fallo en Invariantes de transicion:\n" + res[0] + '\n')