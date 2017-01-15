baseline="-I"
projective=""
#for mwedef in va vb vc

for mwedef in va vb vc
do
for i in `ls ../treebanks/*_train_${mwedef}.conll`
do
  echo $i
  model=$(basename $i)
  model=../models/$model.${mwedef}${baseline}${projective}
  echo $model

  echo java -Xmx16g -cp classes:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -t $i --msize 2000000 $baseline $projective
  java -Xmx16g -cp classes:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -t $i --msize 2000000 $baseline $projective

   model=$(basename $i)
  model=../models/$model.${mwedef}${projective}
  #echo $model

  #echo java -Xmx16g -cp classes:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -t $i --msize 2000000 $projective
  #java -Xmx16g -cp classes:lib/JSAP-2.1.jar fr/upem/lgtools/parser/Parser -m $model -t $i --msize 2000000 $projective
  
done 
done

