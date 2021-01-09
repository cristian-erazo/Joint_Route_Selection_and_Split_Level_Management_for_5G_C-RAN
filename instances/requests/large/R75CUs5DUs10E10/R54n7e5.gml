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
    ant 9
    prb 5
    x 17
    y 64
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 3
    x 41
    y 96
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 4
    prb 6
    x 120
    y 64
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 9
    prb 4
    x 114
    y 112
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 8
    prb 3
    x 50
    y 35
  ]
  edge [
    source 0
    target 6
    bandwith 650
    delay 433
  ]
  edge [
    source 0
    target 5
    bandwith 832
    delay 121
  ]
  edge [
    source 1
    target 3
    bandwith 166
    delay 428
  ]
  edge [
    source 1
    target 4
    bandwith 902
    delay 410
  ]
  edge [
    source 1
    target 2
    bandwith 332
    delay 453
  ]
]
