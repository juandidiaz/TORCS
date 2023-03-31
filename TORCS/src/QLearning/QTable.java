package QLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class QTable {

	Double[][] qTable;
	Random generador;
	Double tasaAprendizaje = 0.3;
	Double factorDescuento = 0.7;

	public QTable() {
		qTable = new Double[Datos.numero_estados][Datos.num_angulos];
		generador = new Random();
		for (int i = 0; i < Datos.numero_estados; i++) {
			for (int j = 0; j < Datos.num_angulos; j++) {
				qTable[i][j] = 0.0;
			}
		}
	}

	public Double getRecompensa(int estado, int accion) {
		return qTable[estado][accion];
	}

	public Double setRecompensa(Integer anterior, Integer actual, Integer actual_accion, Integer anterior_accion,
			Double recompensa, Integer movimientoMejor) {
		Double QAnterior = this.getRecompensa(anterior, anterior_accion);
		Double QEstimada = this.getRecompensa(actual, movimientoMejor);

		QAnterior = (1 - tasaAprendizaje) * QAnterior + tasaAprendizaje * (recompensa + factorDescuento * QEstimada);

		this.qTable[anterior][anterior_accion] = QAnterior;
		return QAnterior;
	}

	public int obtenerMejorMovimiento(Integer estado) {
		// Escoge un movimiento aleatorio.
		int accion = generador.nextInt(Datos.num_angulos);
		// Obtenemos la recompensa actual del mejor movimiento.
		Double maximo = this.getRecompensa(estado, accion);

		// Elegimos la acción que proporciona máxima recompensa en el estado.

		for (int i = 0; i < Datos.num_angulos; i++) {
			Double recompensa = getRecompensa(estado, i);
			if (recompensa > maximo) {
				// Actualizamos la mejor acción y mejor valor.
				accion = i;
				maximo = recompensa;
			}
		}
		return accion;
	}

	public void guardarQTable() {
		File qtable = new File("qtablaDireccion.csv");
		try {
			FileWriter escritor = new FileWriter("qtablaDireccion.csv");
			for (int i = 0; i < Datos.numero_estados; i++) {
				for (int j = 0; j < Datos.num_angulos; j++) {
					if (j < Datos.num_angulos - 1)
						escritor.write(qTable[i][j].toString() + ";");
					else
						escritor.write(qTable[i][j].toString());
				}
				escritor.write("\n");
			}
			escritor.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void CargarQTable() {
		File qtable = new File("qtablaDireccion.csv");
		Scanner reader;
		int contador = 0;
		try {
			reader = new Scanner(qtable);
			while (reader.hasNextLine()) {
				String fila = reader.nextLine();
				String[] datos = fila.split(";");
				for (int i = 0; i < datos.length; i++) {
					this.qTable[contador][i] = Double.parseDouble(datos[i]);
				}
				contador++;
			}
			reader.close();
		} catch (FileNotFoundException ex) {

		}

	}

}
