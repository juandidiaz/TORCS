# Control de un Coche en TORCS usando Q-Learning

Este proyecto implementa un **algoritmo de Q-Learning** (Aprendizaje por Refuerzo) para entrenar un agente que controle un coche en el simulador de carreras TORCS. A través de la exploración y el aprendizaje de acciones, el agente adquiere la capacidad de gestionar las marchas y velocidades para navegar de manera efectiva por diferentes circuitos.

## Descripción del Proyecto

El objetivo de este proyecto es desarrollar un controlador que permita al agente:
- Aprender de sus interacciones con el entorno en TORCS mediante Q-Learning.
- Controlar las marchas y ajustar las velocidades para completar una vuelta en todos los circuitos disponibles.
  
El proyecto incluye las **Q-Tables** obtenidas en el entrenamiento, las cuales se pueden cargar para probar el controlador sin necesidad de entrenarlo desde cero.

## Archivos Incluidos

- **Código de Entrenamiento**: Contiene la implementación del algoritmo de Q-Learning, el proceso de entrenamiento del agente y las funciones para controlar el coche en TORCS.
- **Q-Tables**: Dos tablas de Q aprendidas para probar el controlador en TORCS con diferentes configuraciones. Estas tablas se pueden cargar para evaluar el rendimiento del agente sin realizar un nuevo entrenamiento.

## Objetivos del Agente

1. Completar una vuelta en cada circuito de TORCS, independientemente de las características del trazado.
2. Ajustar automáticamente las marchas y la velocidad para optimizar el tiempo de vuelta y evitar salirse de la pista.

## Requisitos

- **TORCS**: Simulador de carreras utilizado como entorno para el agente.
- **Python 3** y librerías necesarias para ejecutar el algoritmo de Q-Learning y gestionar la interacción con el simulador.

## Cómo Empezar

1. **Instala TORCS** en tu sistema. Configura el simulador para permitir el control mediante código.
2. **Ejecuta el script de entrenamiento** para entrenar al agente desde cero, o **carga una de las Q-Tables** proporcionadas para probar el rendimiento del controlador.
3. Observa cómo el agente controla el coche en el circuito y ajusta su velocidad y marchas en función del entorno.

## Ejecución del Proyecto

Para probar el agente con las Q-Tables ya entrenadas:

1. Carga la Q-Table deseada en el controlador.
2. Ejecuta el script de simulación para ver al agente en acción.

Si deseas entrenar al agente desde el principio, ejecuta el script de entrenamiento y observa cómo el agente mejora con cada episodio.

## Notas

Este proyecto utiliza un enfoque de aprendizaje por refuerzo básico (Q-Learning), lo cual permite la adaptación del agente, aunque puede ser necesario realizar ajustes en los parámetros para mejorar la eficiencia en circuitos más complejos.
