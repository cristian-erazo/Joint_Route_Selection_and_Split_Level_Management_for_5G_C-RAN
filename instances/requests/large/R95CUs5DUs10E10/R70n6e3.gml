graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 3
    x 55
    y 120
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 10
    prb 3
    x 65
    y 28
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 10
    prb 3
    x 49
    y 30
  ]
  edge [
    source 0
    target 5
    bandwith 830
    delay 315
  ]
  edge [
    source 1
    target 3
    bandwith 497
    delay 385
  ]
  edge [
    source 2
    target 4
    bandwith 896
    delay 317
  ]
]
