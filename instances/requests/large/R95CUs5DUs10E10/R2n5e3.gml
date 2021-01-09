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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 4
    prb 2
    x 120
    y 48
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 2
    x 17
    y 21
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 3
    prb 5
    x 107
    y 110
  ]
  edge [
    source 0
    target 2
    bandwith 821
    delay 297
  ]
  edge [
    source 1
    target 4
    bandwith 179
    delay 477
  ]
  edge [
    source 1
    target 3
    bandwith 982
    delay 325
  ]
]
