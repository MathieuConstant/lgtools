for i in `ls ../treebanks/*_dev_v0.conll`
do
  echo $i
  model=$(basename $i)
  model=${model%_*_*}
  model=../models/${model}_train_v0.conll
  echo $model
  java -Xmx8g -cp bin:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -i $i -B
  break
done 


