graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 6
    prb 3
    x 66
    y 45
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 9
    prb 4
    x 34
    y 55
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 6
    prb 4
    x 99
    y 12
  ]
  edge [
    source 0
    target 4
    bandwith 432
    delay 194
  ]
  edge [
    source 1
    target 2
    bandwith 309
    delay 147
  ]
  edge [
    source 1
    target 3
    bandwith 596
    delay 140
  ]
]
