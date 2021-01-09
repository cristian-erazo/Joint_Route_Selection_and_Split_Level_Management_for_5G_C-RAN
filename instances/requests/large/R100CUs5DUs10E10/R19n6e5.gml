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
    type 1
    prc 3
    ant 5
    prb 3
    x 80
    y 30
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 6
    prb 2
    x 61
    y 116
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 4
    x 77
    y 42
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 8
    prb 6
    x 110
    y 77
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 10
    prb 4
    x 28
    y 107
  ]
  edge [
    source 0
    target 4
    bandwith 729
    delay 142
  ]
  edge [
    source 0
    target 1
    bandwith 207
    delay 407
  ]
  edge [
    source 0
    target 2
    bandwith 329
    delay 483
  ]
  edge [
    source 0
    target 5
    bandwith 200
    delay 268
  ]
  edge [
    source 0
    target 3
    bandwith 298
    delay 391
  ]
]
