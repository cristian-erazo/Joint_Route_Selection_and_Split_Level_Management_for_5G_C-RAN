graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    type 1
    prc 4
    ant 3
    prb 2
    x 36
    y 113
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 9
    prb 2
    x 72
    y 47
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 6
    prb 5
    x 19
    y 86
  ]
  edge [
    source 0
    target 4
    bandwith 985
    delay 144
  ]
  edge [
    source 1
    target 3
    bandwith 733
    delay 223
  ]
  edge [
    source 1
    target 2
    bandwith 396
    delay 353
  ]
]
