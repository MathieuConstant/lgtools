for i in `ls ../treebanks/*_train_vb.conll`
do
  echo $i
  model=$(basename $i)
  model=../models/$model
  echo $model

  echo java -Xmx8g -cp bin:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -t $i  --iters 1
  java -Xmx8g -cp bin:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -t $i  --iters 1
  
done 


