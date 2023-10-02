package ao.znt.econ.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import ao.znt.econ.R;
import ao.znt.econ.adapters.RecicleViewsAdapter;
import ao.znt.econ.dao.DatabaseHelper;
import ao.znt.econ.dao.OrcamentoDao;
import ao.znt.econ.modelos.Orcamento;
import ao.znt.econ.util.Util;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class Estatistica extends AppCompatActivity {

    private ColumnChartView columnChartView;
    private RecicleViewsAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    private OrcamentoDao dao;
    private ArrayList<Orcamento> orcamentosCorrente;
    public ArrayList<Orcamento> orcamentosFicticio;
    private FloatingActionButton floatingActionButton;
    private TextView textDescricao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_estatistica);
        new Util(this);

        textDescricao = findViewById(R.id.text_estatistica_descricao);
        final Toolbar mToolbar = findViewById(R.id.toolbarEstatistica);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        floatingActionButton= findViewById(R.id.fab_estatistica_add_pri_orc);

        DatabaseHelper helper = new DatabaseHelper(this);
        try {
            dao = new OrcamentoDao(helper.getConnectionSource());
            initializeData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recyclerView = findViewById(R.id.recyclerView_horizontal);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        columnChartView = findViewById(R.id.chart);

        adapter = new RecicleViewsAdapter(orcamentosCorrente);
    }

    private ArrayList<String> initColunaNomesCorrente() {
        ArrayList<String> nomes = new ArrayList<>();
       for (ao.znt.econ.modelos.Orcamento orc : orcamentosCorrente){
           nomes.add(Util.getCategoria(orc.getCategoria()));
       }
        return nomes;
    }
    private ArrayList<String> initColunaNomesFicticio() {
        ArrayList<String> nomes = new ArrayList<>();
        for (ao.znt.econ.modelos.Orcamento orc : orcamentosFicticio){
            nomes.add(Util.getCategoria(orc.getCategoria()));
        }
        return nomes;
    }

    private void initializeData(){
        orcamentosFicticio = new ArrayList<>();//para quando nao ouver orcs resistado
        orcamentosCorrente = new ArrayList<>();
        try {//Só os orcs do mes corrente
            //TODO
            Calendar c = Calendar.getInstance();
            int anoAtual = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH);
            int mesActual = mes + 1;

            ArrayList<Orcamento> orcs = (ArrayList<Orcamento>) dao.queryForAll();

            for(ao.znt.econ.modelos.Orcamento orcamento : orcs){
                System.out.println("ORC ainda N ADD "+anoAtual);
                if ((Util.getMes(orcamento.getData())==mesActual) && (Util.getAno(orcamento.getData()) == anoAtual)) {
                    orcamentosCorrente.add(orcamento);
                    System.out.println("ORC ja ADD "+Util.getMesString(Util.getMes(orcamento.getData())));
                }
            }
            //orcamentos = (ArrayList<Orcamento>) dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //init Para quando n ouver orc
        for (String c : getResources().getStringArray(R.array.categorias)){
            ao.znt.econ.modelos.Orcamento o = new ao.znt.econ.modelos.Orcamento();
            o.setCategoria(Util.getPositionCategoria(c));
            orcamentosFicticio.add(o);
        }
    }

    private void initializeAdapter(){
        adapter.updateList(orcamentosCorrente);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
        initChartView();
        initializeAdapter();
    }
    @SuppressLint("SetTextI18n")
    private void initChartView(){
        textDescricao.setText(getString(R.string.analise_dos_orcs_do_mes_actual)+Util.getMesString(Util.getMes(Calendar.getInstance().getTime())));

        int nColuna;
        List<ao.znt.econ.modelos.Orcamento> orcamentos ;
        ArrayList<String> colunasNome;
        if(orcamentosCorrente.size()!=0){
            System.out.println("ORC !ZERO");
            colunasNome = initColunaNomesCorrente();
            nColuna = orcamentosCorrente.size();
            orcamentos = orcamentosCorrente;
            //fab Visivel
            RelativeLayout relativeLayout = (RelativeLayout) floatingActionButton.getParent();
            relativeLayout.setVisibility(View.GONE);
        }else{
            //todo
            System.out.println("ORC ZERO");
            colunasNome = initColunaNomesFicticio();
            nColuna = orcamentosFicticio.size();
            orcamentos = orcamentosFicticio;

            //fab Visivel
            RelativeLayout relativeLayout = (RelativeLayout) floatingActionButton.getParent();
            relativeLayout.setVisibility(View.VISIBLE);
        }

        int nSubColuna = 1;

        List<Column> colunas = new ArrayList<>();
        List<SubcolumnValue> subcolumnValues;
        List<AxisValue> axisValues = new ArrayList<>();

        for(int i = 0;i<nColuna;i++){
            subcolumnValues = new ArrayList<>();
            for (int j = 0; j < nSubColuna;j++){
                subcolumnValues.add(new SubcolumnValue((float) orcamentos.get(i).getSaldoGasto(), ChartUtils.pickColor()));
            }
            Column column = new Column(subcolumnValues);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            colunas.add(column);

            axisValues.add(new AxisValue(i).setLabel(colunasNome.get(i)));
        }
        ColumnChartData columnChartData = new ColumnChartData(colunas);

        //X
        Axis axisX = new Axis();
        axisX.hasLines();
        axisX.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        axisX.setValues(axisValues);
        axisX.setTextSize(10);
        //axisX.setHasTiltedLabels(true);
        //Y
        Axis axisY = new Axis();
        //axisY.hasLines();
        axisY.setTextColor(Color.GRAY);
        //axisY.setValues(axisValues);
        //axisY.setName("Análise dos gastos deste mes"/*+orcamento.getMesString()*/);
        //axisY.setHasTiltedLabels(true);

        columnChartData.setFillRatio(0.8f);
        columnChartData.setValueLabelTextSize(8);
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);

        columnChartView.setColumnChartData(columnChartData);
    }

    public void primeiroOrcamentoEstatistica(View view) {
        startActivity(new Intent(this, ao.znt.econ.ui.Orcamento.class));
    }
}