package evaluable1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Classe principal per a la gestio de fitxers.
 */

public class ae1 extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtCoincidencies;
	private JTextField txtNouFitxer;
	private JTextField txtDirectori;
	private static String ruta = "";
	@SuppressWarnings("rawtypes")
	private static JList seleccioArxius;
	private static boolean ascendent = true;
	private static String criteriOrden = "Nom";
	private static FileFilter txtFileFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			return file.isFile() && file.getName().toLowerCase().endsWith(".txt");
		}
	};
	
	
	/**
     * Constructor de la classe ae1 que inicialitza la finestra i els seus components.
     */

	@SuppressWarnings("rawtypes")
	public ae1() {

		setTitle("Gestió de fitxers");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 888, 478);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(36, 69, 803, 237);
		contentPane.add(scrollPane);

		seleccioArxius = new JList();
		scrollPane.setViewportView(seleccioArxius);

		JButton btnCarregar = new JButton("Carregar");
		btnCarregar.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCarregar.setBounds(36, 316, 212, 26);
		contentPane.add(btnCarregar);

		btnCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregaLlista();
			}
		});

		JComboBox<String> cmbOrdenar = new JComboBox<>();
		cmbOrdenar.setFont(new Font("Tahoma", Font.PLAIN, 12));
		cmbOrdenar.setModel(new DefaultComboBoxModel<>(new String[] { "Nom", "Tamany", "Data Modificació" }));
		cmbOrdenar.setBounds(379, 316, 163, 21);
		contentPane.add(cmbOrdenar);

		cmbOrdenar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String nuevoCriterio = (String) cmbOrdenar.getSelectedItem();
				criteriOrden = nuevoCriterio;
			}
		});

		JLabel lblOrdenarPer = new JLabel("Ordenar per...");
		lblOrdenarPer.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblOrdenarPer.setBounds(293, 320, 86, 13);
		contentPane.add(lblOrdenarPer);

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButton rdbtnAscendent = new JRadioButton("Ascendent");
		rdbtnAscendent.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtnAscendent.setSelected(true);
		rdbtnAscendent.setBounds(616, 316, 103, 21);
		contentPane.add(rdbtnAscendent);
		buttonGroup.add(rdbtnAscendent);

		rdbtnAscendent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnAscendent.isSelected()) {
					ascendent = true;
				}
			}
		});

		JRadioButton rdbtnDescendent = new JRadioButton("Descendent");
		rdbtnDescendent.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtnDescendent.setBounds(724, 316, 103, 21);
		contentPane.add(rdbtnDescendent);
		buttonGroup.add(rdbtnDescendent);

		rdbtnDescendent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnDescendent.isSelected()) {
					ascendent = false;
				}
			}
		});

		txtCoincidencies = new JTextField();
		txtCoincidencies.setBounds(36, 352, 537, 26);
		contentPane.add(txtCoincidencies);
		txtCoincidencies.setColumns(10);

		JButton btnBuscarCoincidencies = new JButton("Cercar Coincidències");

		btnBuscarCoincidencies.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {

				DefaultListModel<String> llistaCoincidencies = new DefaultListModel<String>();
				File carpeta = new File(ruta);
				File[] llistaArxius = carpeta.listFiles(txtFileFilter);
				String paraulaBuscada = txtCoincidencies.getText();

				if (!paraulaBuscada.equals("")) {
					for (File arxiu : llistaArxius) {

						String nomFitxer = arxiu.getName();
						String info = nomFitxer;

						int coincidencies = comptaCoincidencies(paraulaBuscada, arxiu);

						info += " --> Coincidències amb '" + paraulaBuscada + "': " + coincidencies;

						llistaCoincidencies.addElement(info);
					}

					seleccioArxius.setModel(llistaCoincidencies);
				}
			}
		});

		btnBuscarCoincidencies.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnBuscarCoincidencies.setBounds(588, 352, 251, 26);
		contentPane.add(btnBuscarCoincidencies);

		JLabel lblNomNouFitxer = new JLabel("Nom del nou fitxer:");
		lblNomNouFitxer.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNomNouFitxer.setBounds(36, 398, 135, 26);
		contentPane.add(lblNomNouFitxer);

		txtNouFitxer = new JTextField();
		txtNouFitxer.setColumns(10);
		txtNouFitxer.setBounds(157, 400, 527, 26);
		contentPane.add(txtNouFitxer);

		JButton btnFusionarFitxers = new JButton("Fusionar Fitxers");

		btnFusionarFitxers.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {

				String nomNouFitxer = txtNouFitxer.getText();
				ArrayList<String> archivosSeleccionados = new ArrayList<>();
				Object[] seleccionados = seleccioArxius.getSelectedValues();

				for (Object seleccionado : seleccionados) {
					String elemento = (String) seleccionado;
					String nombreArchivo = obtindreNomArxiu(elemento);
					archivosSeleccionados.add(nombreArchivo);
				}

				if (archivosSeleccionados.size() > 1) {
					if (FusionaFitxers(archivosSeleccionados, nomNouFitxer)) {
						JOptionPane.showMessageDialog(null, "Fusió realitzada correctament", "Operació realitzada",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "El fitxer introduït ja existeix", "Fusió incorrecta",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Has de seleccionar més d'un arxiu per a poder fusionar.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		btnFusionarFitxers.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnFusionarFitxers.setBounds(711, 398, 128, 26);
		contentPane.add(btnFusionarFitxers);

		txtDirectori = new JTextField();
		txtDirectori.setEditable(false);
		txtDirectori.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtDirectori.setColumns(10);
		txtDirectori.setBounds(36, 20, 537, 26);
		contentPane.add(txtDirectori);
		
		
		

		JButton btnSeleccionarDirectori = new JButton("Seleccionar Directori...");
		btnSeleccionarDirectori.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSeleccionarDirectori.setBounds(588, 20, 251, 26);
		contentPane.add(btnSeleccionarDirectori);

		btnSeleccionarDirectori.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				seleccionarDirectorio();
				txtDirectori.setText(ruta);
			}
		});

	}
	
	
	/**
     * Metode per seleccionar un directori mitjançant un explorador de fitxers.
     */
	private void seleccionarDirectorio() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedDirectory = fileChooser.getSelectedFile();
			ruta = selectedDirectory.getAbsolutePath();
			carregaLlista();
		}
	}
	
	
	
	
	/**
     * Metode per carregar la llista d'arxius del directori seleccionat.
     */
	@SuppressWarnings("unchecked")
	public void carregaLlista() {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss");
		File carpeta = new File(ruta);

		File[] llistaArxius = carpeta.listFiles(txtFileFilter);

		Arrays.sort(llistaArxius, (f1, f2) -> {
			int comparador = 0;
			switch (criteriOrden) {
			case "Nom":
				comparador = f1.getName().compareToIgnoreCase(f2.getName());
				break;
			case "Tamany":
				comparador = Long.compare(f1.length(), f2.length());
				break;
			case "Data Modificació":
				comparador = Long.compare(f1.lastModified(), f2.lastModified());
				break;
			}
			return ascendent ? comparador : -comparador;
		});

		for (File arxiu : llistaArxius) {
			String nomFitx = arxiu.getName();
			long tamany = arxiu.length();
			String ultimaModif = dateFormat.format(arxiu.lastModified());
			String extensio = nomFitx.substring(nomFitx.lastIndexOf(".") + 1);

			String info = String.format("%s | Modificat: %s | Tamany: %d bytes | Extensió: %s", nomFitx, ultimaModif,
					tamany, extensio);

			listModel.addElement(info);
		}

		seleccioArxius.setModel(listModel);
		seleccioArxius.repaint();
	}
	
	
	
	
	
	/**
     * Metode per comptar les coincidencies d'una paraula en un arxiu.
     * 
     * @param paraula La paraula a buscar.
     * @param arxiu   L'arxiu en el qual buscar les coincidencies.
     * @return El nombre de coincidencies de la paraula en l'arxiu.
     */

	public int comptaCoincidencies(String paraula, File arxiu) {

		int cont = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(arxiu))) {

			String linea;
			Pattern pattern = Pattern.compile("\\b" + Pattern.quote(paraula) + "\\b", Pattern.CASE_INSENSITIVE);

			while ((linea = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(linea);
				while (matcher.find()) {
					cont++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cont;
	}
	
	
	
	
	
	/**
     * Metode per fusionar diversos arxius en un nou fitxer.
     * 
     * @param llistaNoms La llista dels noms dels arxius a fusionar.
     * @param nouFitx    El nom del nou fitxer creat com a resultat de la fusio.
     * @return True si la fusio s'ha realitzat amb exit, False si ja existeix el fitxer.
     */

	public boolean FusionaFitxers(ArrayList<String> llistaNoms, String nouFitx) {

		boolean ok = false;
		File nouFitxer = new File("./src/evaluable1/dir/" + nouFitx + ".txt");
		if (!nouFitxer.exists()) {

			ok = true;

			try {

				FileWriter fw = new FileWriter(nouFitxer);
				BufferedWriter bw = new BufferedWriter(fw);

				for (String nomFitx : llistaNoms) {
					File fitxerActual = new File("./src/evaluable1/dir/" + nomFitx);

					if (fitxerActual.exists() && fitxerActual.isFile()) {

						try (FileReader fr = new FileReader(fitxerActual); BufferedReader br = new BufferedReader(fr)) {

							String linia;
							while ((linia = br.readLine()) != null) {
								bw.write(linia);
								bw.newLine();
							}

							br.close();
							fr.close();
						}
					}
				}

				bw.close();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			ok = false;
		}

		return ok;

	}
	
	
	
	
	/**
     * Metode per obtenir el nom de l'arxiu a partir de la informacio.
     * 
     * @param info La informacio de l'arxiu.
     * @return El nom de l'arxiu.
     */

	private String obtindreNomArxiu(String info) {

		int index = info.indexOf(".txt");
		if (index != -1) {
			return info.substring(0, index + 4);
		}
		return info;
	}
	
	
	
	/**
     * Metode principal per a l'execucio de l'aplicacio.
     * 
     * @param args Arguments del programa.
     */

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				ae1 frame = new ae1();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}