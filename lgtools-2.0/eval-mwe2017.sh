section="dev"

#for version in v0 va vb vc
#for version in va vb vc
for version in v0
do
for i in `ls ../treebanks/*_${section}_${version}.conll`
do
#for baseline in "" "-B"
for baseline in "-B"
do
  echo $i
  model=$(basename $i)
  modeltmp=${model%_*_*}
  suffix=${version}.conll.${version}$baseline

  model=../models/${modeltmp}_train_${suffix}  
  output=../outputs/${modeltmp}_${section}_${suffix}.conll
  echo $model
  echo $output
  echo java -Xmx16g -cp classes:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -o $output -i $i $baseline
  java -Xmx16g -cp classes:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -o $output -i $i $baseline
done
done 
done

